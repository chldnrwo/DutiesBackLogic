package alpha;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class 월화수목금_토일공휴일_로직 {

    public static void main(String[] args) {
        List<String> members = Arrays.asList("CB", "CW", "JM", "JS", "JY", "KJ", "LS", "SS");

        // 현재 날짜를 기준으로 다음 달의 월을 가져옴
        LocalDate currentDate = LocalDate.now().plusMonths(1);
        YearMonth nextMonth = YearMonth.of(currentDate.getYear(), currentDate.getMonth());
        int daysInMonth = nextMonth.lengthOfMonth();

        // 당월 휴가 목록
        List<Vacation> vacationSchedule = Arrays.asList(
            new Vacation(1, 1, "SS"),
            new Vacation(7, 9, "SS"),
            
            new Vacation(1, 7, "LS"),
       
            new Vacation(6, 8, "JS"),
            
            new Vacation(6, 6, "JY"),
            new Vacation(27, 27, "JY"),
            
            new Vacation(12, 15, "KJ"),
            
            new Vacation(19, 22, "CW"),
            
            new Vacation(20, 22, "JM"),

            new Vacation(6, 8, "CB"),
            new Vacation(28, 30, "CB")
        );

        Map<String, Integer> dutyCounts = new HashMap<>();
        Map<String, Integer> weekendDutyCounts = new HashMap<>();
        Map<String, Integer> lastDutyDay = new HashMap<>();
        Map<String, Integer> mainDutyCounts = new HashMap<>();  // 메인 근무 횟수 카운트
        Map<String, Integer> subDutyCounts = new HashMap<>();   // 서브 근무 횟수 카운트
        for (String member : members) {
            dutyCounts.put(member, 0);
            weekendDutyCounts.put(member, 0);
            lastDutyDay.put(member, -1);
            mainDutyCounts.put(member, 0);
            subDutyCounts.put(member, 0);
        }
        dutyCounts.put("LS", 2);
        mainDutyCounts.put("LS", 1);
        subDutyCounts.put("LS", 1);   
        
        // 메인 함수 호출
        List<Schedule> schedule = createSchedule(members, vacationSchedule, nextMonth, daysInMonth, dutyCounts, weekendDutyCounts, lastDutyDay, mainDutyCounts, subDutyCounts);

        // 스케줄 출력
        System.out.println("Schedule for " + nextMonth.getMonth() + " " + nextMonth.getYear());
        for (Schedule s : schedule) {
            System.out.println("Day " + s.day + ": Main: " + s.main + ", Sub: " + s.sub);
        }

        // 근무 횟수 통계를 위한 맵
        Map<String, Integer> weekdayCounts = new HashMap<>();
        Map<String, Integer> weekendCounts = new HashMap<>();
        Map<String, Integer> totalCounts = new HashMap<>();

        for (String member : members) {
            weekdayCounts.put(member, 0);
            weekendCounts.put(member, 0);
            totalCounts.put(member, 0);
        }
        
        
        // 근무 횟수 계산
        for (Schedule s : schedule) {
            if (isWeekend(s.day, nextMonth)) {
                weekendCounts.put(s.main, weekendCounts.get(s.main) + 1);
                weekendCounts.put(s.sub, weekendCounts.get(s.sub) + 1);
            } else {
                weekdayCounts.put(s.main, weekdayCounts.get(s.main) + 1);
                weekdayCounts.put(s.sub, weekdayCounts.get(s.sub) + 1);
            }
            totalCounts.put(s.main, totalCounts.get(s.main) + 1);
            totalCounts.put(s.sub, totalCounts.get(s.sub) + 1);
        }

        // 근무 통계 출력
        System.out.println("\nLS : 보정 2 day");
        System.out.println("사유 : 나주 출장");
        System.out.println("Member-wise Duty Distribution:");
        System.out.println("Member | Weekday Duties | Weekend/holiday Duties | Total Duties | Main Duties | Sub Duties");
        System.out.println("--------------------------------------------------------------------------------");
        for (String member : members) {
            System.out.printf("%-6s | %-14d | %-22d | %-12d | %-11d | %-10d%n", 
                              member, 
                              weekdayCounts.get(member), 
                              weekendCounts.get(member), 
                              totalCounts.get(member),
                              mainDutyCounts.get(member), 
                              subDutyCounts.get(member));
        }
    }

    // 스케줄 생성 함수
    public static List<Schedule> createSchedule(List<String> members, List<Vacation> vacationSchedule, YearMonth nextMonth, int daysInMonth,
                                                Map<String, Integer> dutyCounts, Map<String, Integer> weekendDutyCounts, 
                                                Map<String, Integer> lastDutyDay, Map<String, Integer> mainDutyCounts, 
                                                Map<String, Integer> subDutyCounts) {
        List<Schedule> schedule = new ArrayList<>();
        Map<Integer, List<String>> vacationDays = new HashMap<>();

        for (Vacation vacation : vacationSchedule) {
            for (int day = vacation.start; day <= vacation.end; day++) {
                vacationDays.computeIfAbsent(day, k -> new ArrayList<>()).add(vacation.name);
            }
        }

        for (int i = 1; i <= daysInMonth; i++) {
            final int day = i;

            if (isWeekend(day, nextMonth)) {
                // 주말 메인 담당자 배정
                String main = assignDuty(members, day, schedule, vacationDays, weekendDutyCounts, lastDutyDay, mainDutyCounts, subDutyCounts, true, null, day - 1);
                if (main == null) continue;

                // 주말 서브 담당자 배정
                String sub = assignDuty(members, day, schedule, vacationDays, weekendDutyCounts, lastDutyDay, mainDutyCounts, subDutyCounts, false, main, day - 1);
                if (sub == null) continue;

                // 동일 주말에 같은 사람의 두 번 근무 방지
                if (main.equals(sub)) {
                    continue;  // 같은 사람일 경우 다시 배정하도록 넘어갑니다.
                }

                schedule.add(new Schedule(day, main, sub));
                lastDutyDay.put(main, day);
                lastDutyDay.put(sub, day);
                weekendDutyCounts.put(main, weekendDutyCounts.get(main) + 1);
                weekendDutyCounts.put(sub, weekendDutyCounts.get(sub) + 1);
                dutyCounts.put(main, dutyCounts.get(main) + 1);
                dutyCounts.put(sub, dutyCounts.get(sub) + 1);
                mainDutyCounts.put(main, mainDutyCounts.get(main) + 1);  // 메인 근무 횟수 증가
                subDutyCounts.put(sub, subDutyCounts.get(sub) + 1);    // 서브 근무 횟수 증가
            } else {
                // 평일 메인 담당자 배정
                String main = assignDuty(members, day, schedule, vacationDays, dutyCounts, lastDutyDay, mainDutyCounts, subDutyCounts, true, null, day - 2, day - 1);
                if (main == null) continue;

                // 평일 서브 담당자 배정
                String sub = assignDuty(members, day, schedule, vacationDays, dutyCounts, lastDutyDay, mainDutyCounts, subDutyCounts, false, main, day - 2, day - 1);
                if (sub == null) continue;

                schedule.add(new Schedule(day, main, sub));
                lastDutyDay.put(main, day);
                lastDutyDay.put(sub, day);
                dutyCounts.put(main, dutyCounts.get(main) + 1);
                dutyCounts.put(sub, dutyCounts.get(sub) + 1);
                mainDutyCounts.put(main, mainDutyCounts.get(main) + 1);  // 메인 근무 횟수 증가
                subDutyCounts.put(sub, subDutyCounts.get(sub) + 1);    // 서브 근무 횟수 증가
            }
        }

        return schedule;
    }

    // 담당자 배정 함수
    private static String assignDuty(List<String> members, int day, List<Schedule> schedule, Map<Integer, List<String>> vacationDays,
                                     Map<String, Integer> dutyCounts, Map<String, Integer> lastDutyDay, Map<String, Integer> mainDutyCounts, 
                                     Map<String, Integer> subDutyCounts, boolean isMain,
                                     String exclude, int... checkDays) {
        List<String> candidates = new ArrayList<>();
        Set<String> excludeSet = new HashSet<>();
        if (exclude != null) {
            excludeSet.add(exclude);
        }

        // 체크할 날의 스케줄을 가져와서 제외할 팀원들을 추출
        for (int checkDay : checkDays) {
            if (checkDay > 0) {
                schedule.stream()
                        .filter(s -> s.day == checkDay)
                        .flatMap(s -> Stream.of(s.main, s.sub))
                        .map(String.class::cast)  // String 타입으로 캐스팅
                        .forEach(excludeSet::add);
            }
        }

        for (String member : members) {
            if (!excludeSet.contains(member)
                    && !isOnVacation(vacationDays, member, day)
                    && !hasRecentDuty(schedule, member, day, lastDutyDay, checkDays)) {
                candidates.add(member);
            }
        }

        if (candidates.isEmpty()) {
            return null;
        }

        // 메인과 서브의 편중을 막기 위해 메인/서브 근무 횟수 차이를 고려한 정렬
        if (isMain) {
            candidates.sort(Comparator.comparingInt(mainDutyCounts::get)
                    .thenComparingInt(dutyCounts::get)
                    .thenComparingInt(lastDutyDay::get));
        } else {
            candidates.sort(Comparator.comparingInt(subDutyCounts::get)
                    .thenComparingInt(dutyCounts::get)
                    .thenComparingInt(lastDutyDay::get));
        }

        // 랜덤하게 동점 처리
        List<String> topCandidates = new ArrayList<>();
        int minDuty = isMain ? mainDutyCounts.get(candidates.get(0)) : subDutyCounts.get(candidates.get(0));
        int minLastDay = lastDutyDay.get(candidates.get(0));

        for (String candidate : candidates) {
            if ((isMain ? mainDutyCounts.get(candidate) : subDutyCounts.get(candidate)) == minDuty
                && lastDutyDay.get(candidate) == minLastDay) {
                topCandidates.add(candidate);
            } else {
                break;
            }
        }

        Collections.shuffle(topCandidates);
        return topCandidates.get(0);
    }

    // 실제 주말인지 확인하는 함수
    private static boolean isWeekend(int day, YearMonth month) {
        if(YearMonth.of(2024, 9).equals(month) && (day==16 || day==17 || day==18)){
           return true;
        }

        
        LocalDate date = month.atDay(day);
        return date.getDayOfWeek().getValue() == 6 || date.getDayOfWeek().getValue() == 7;
    }

    private static boolean isOnVacation(Map<Integer, List<String>> vacationDays, String member, int day) {
        return vacationDays.containsKey(day) && vacationDays.get(day).contains(member);
    }

    private static boolean hasRecentDuty(List<Schedule> schedule, String member, int day, Map<String, Integer> lastDutyDay, int... checkDays) {
        int lastDay = lastDutyDay.getOrDefault(member, -1);
        if (lastDay != -1 && (day - lastDay) <= 1) {
            return true;
        }

        for (int checkDay : checkDays) {
            if (checkDay > 0) {
                Schedule previousSchedule = schedule.stream().filter(s -> s.day == checkDay).findFirst().orElse(null);
                if (previousSchedule != null && (member.equals(previousSchedule.main) || member.equals(previousSchedule.sub))) {
                    return true;
                }
            }
        }

        return false;
    }

    

    static class Vacation {
        int start, end;
        String name;

        Vacation(int start, int end, String name) {
            this.start = start;
            this.end = end;
            this.name = name;
        }
    }

    static class Schedule {
        int day;
        String main, sub;

        Schedule(int day, String main, String sub) {
            this.day = day;
            this.main = main;
            this.sub = sub;
        }
    }
}
