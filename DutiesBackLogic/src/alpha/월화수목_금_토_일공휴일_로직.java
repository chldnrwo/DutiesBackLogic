package alpha;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class 월화수목_금_토_일공휴일_로직 {

    public static void main(String[] args) {
        List<String> members = Arrays.asList("CB", "CW", "JM", "JS", "JY", "KJ", "LS", "SS");

        // 현재 날짜를 기준으로 다음 달의 월을 가져옴
        //LocalDate currentDate = LocalDate.now().plusMonths(1);
        LocalDate currentDate = LocalDate.now();
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
        Map<String, Integer> fridayDutyCounts = new HashMap<>(); // 금요일 근무 횟수
        Map<String, Integer> lastDutyDay = new HashMap<>();
        Map<String, Integer> mainDutyCounts = new HashMap<>();  // 메인 근무 횟수 카운트
        Map<String, Integer> subDutyCounts = new HashMap<>();   // 서브 근무 횟수 카운트
        for (String member : members) {
            dutyCounts.put(member, 0);
            weekendDutyCounts.put(member, 0);
            fridayDutyCounts.put(member, 0); // 금요일 근무 횟수 초기화
            lastDutyDay.put(member, -1);
            mainDutyCounts.put(member, 0);
            subDutyCounts.put(member, 0);
        }

        // 메인 함수 호출
        List<Schedule> schedule = createSchedule(members, vacationSchedule, nextMonth, daysInMonth, dutyCounts, weekendDutyCounts, fridayDutyCounts, lastDutyDay, mainDutyCounts, subDutyCounts);

        // 스케줄 출력
        System.out.println("Schedule for " + nextMonth.getMonth() + " " + nextMonth.getYear());
        for (Schedule s : schedule) {
            System.out.println("Day " + s.day + ": Main: " + s.main + ", Sub: " + s.sub);
        }
        Map<String, Integer> weekdayCounts = new HashMap<>();  // 월화수목 근무 횟수
        Map<String, Integer> fridayCounts = new HashMap<>();   // 금요일 근무 횟수
        Map<String, Integer> saturdayCounts = new HashMap<>(); // 토요일 근무 횟수
        Map<String, Integer> sundayCounts = new HashMap<>();   // 일요일 및 공휴일 근무 횟수
        
        // 초기화
        for (String member : members) {
            weekdayCounts.put(member, 0);
            fridayCounts.put(member, 0);
            saturdayCounts.put(member, 0);
            sundayCounts.put(member, 0);
            // mainDutyCounts와 subDutyCounts는 이미 선언되었으므로 초기화만 수행
            mainDutyCounts.put(member, 0);
            subDutyCounts.put(member, 0);
        }
        
        // 근무 횟수 계산
        for (Schedule s : schedule) {
            if (isFriday(s.day, nextMonth)) {
                // 금요일 근무 카운트
                fridayCounts.put(s.main, fridayCounts.get(s.main) + 1);
                fridayCounts.put(s.sub, fridayCounts.get(s.sub) + 1);
            } else if (isSaturday(s.day, nextMonth)) {
                // 토요일 근무 카운트
                saturdayCounts.put(s.main, saturdayCounts.get(s.main) + 1);
                saturdayCounts.put(s.sub, saturdayCounts.get(s.sub) + 1);
            } else if (isWeekend(s.day, nextMonth)) {
                // 일요일 및 공휴일 근무 카운트
                sundayCounts.put(s.main, sundayCounts.get(s.main) + 1);
                sundayCounts.put(s.sub, sundayCounts.get(s.sub) + 1);
            } else {
                // 월화수목 근무 카운트
                weekdayCounts.put(s.main, weekdayCounts.get(s.main) + 1);
                weekdayCounts.put(s.sub, weekdayCounts.get(s.sub) + 1);
            }
        
            // 메인/서브 담당 횟수 카운트
            mainDutyCounts.put(s.main, mainDutyCounts.get(s.main) + 1);
            subDutyCounts.put(s.sub, subDutyCounts.get(s.sub) + 1);
        }
        
        // 근무 통계 출력
        System.out.println("\nMember-wise Duty Distribution:");
        System.out.println("Member | Weekday Duties | Friday Duties | Saturday Duties | Sunday/Holiday Duties | Main Duties | Sub Duties");
        System.out.println("-----------------------------------------------------------------------------------------------------------");
        
        for (String member : members) {
            System.out.printf("%-6s | %-14d | %-13d | %-15d | %-22d | %-11d | %-10d%n", 
                              member, 
                              weekdayCounts.get(member),      // 월화수목 근무 횟수
                              fridayCounts.get(member),       // 금요일 근무 횟수
                              saturdayCounts.get(member),     // 토요일 근무 횟수
                              sundayCounts.get(member),       // 일요일 및 공휴일 근무 횟수
                              mainDutyCounts.get(member),     // 메인 담당 횟수
                              subDutyCounts.get(member));     // 서브 담당 횟수
        }
    }

    // 스케줄 생성 함수
    public static List<Schedule> createSchedule(List<String> members, List<Vacation> vacationSchedule, YearMonth nextMonth, int daysInMonth,
                                                Map<String, Integer> dutyCounts, Map<String, Integer> weekendDutyCounts, 
                                                Map<String, Integer> fridayDutyCounts, Map<String, Integer> lastDutyDay, 
                                                Map<String, Integer> mainDutyCounts, Map<String, Integer> subDutyCounts) {
        List<Schedule> schedule = new ArrayList<>();
        Map<Integer, List<String>> vacationDays = new HashMap<>();

        for (Vacation vacation : vacationSchedule) {
            for (int day = vacation.start; day <= vacation.end; day++) {
                vacationDays.computeIfAbsent(day, k -> new ArrayList<>()).add(vacation.name);
            }
        }

        for (int i = 1; i <= daysInMonth; i++) {
            final int day = i;

            if (isFriday(day, nextMonth)) {
                // 금요일 메인 담당자 배정
                String main = assignDuty(members, day, schedule, vacationDays, fridayDutyCounts, lastDutyDay, mainDutyCounts, subDutyCounts, true, null, day - 1);
                if (main == null) continue;

                // 금요일 서브 담당자 배정
                String sub = assignDuty(members, day, schedule, vacationDays, fridayDutyCounts, lastDutyDay, mainDutyCounts, subDutyCounts, false, main, day - 1);
                if (sub == null) continue;

                schedule.add(new Schedule(day, main, sub));
                lastDutyDay.put(main, day);
                lastDutyDay.put(sub, day);
                fridayDutyCounts.put(main, fridayDutyCounts.get(main) + 1);
                fridayDutyCounts.put(sub, fridayDutyCounts.get(sub) + 1);
                dutyCounts.put(main, dutyCounts.get(main) + 1);
                dutyCounts.put(sub, dutyCounts.get(sub) + 1);
                mainDutyCounts.put(main, mainDutyCounts.get(main) + 1);  // 메인 근무 횟수 증가
                subDutyCounts.put(sub, subDutyCounts.get(sub) + 1);      // 서브 근무 횟수 증가
            } else if (isSaturday(day, nextMonth)) {
                // 토요일 메인 담당자 배정
                String main = assignDuty(members, day, schedule, vacationDays, weekendDutyCounts, lastDutyDay, mainDutyCounts, subDutyCounts, true, null, day - 1);
                if (main == null) continue;

                // 토요일 서브 담당자 배정
                String sub = assignDuty(members, day, schedule, vacationDays, weekendDutyCounts, lastDutyDay, mainDutyCounts, subDutyCounts, false, main, day - 1);
                if (sub == null) continue;

                schedule.add(new Schedule(day, main, sub));
                lastDutyDay.put(main, day);
                lastDutyDay.put(sub, day);
                weekendDutyCounts.put(main, weekendDutyCounts.get(main) + 1);
                weekendDutyCounts.put(sub, weekendDutyCounts.get(sub) + 1);
                dutyCounts.put(main, dutyCounts.get(main) + 1);
                dutyCounts.put(sub, dutyCounts.get(sub) + 1);
                mainDutyCounts.put(main, mainDutyCounts.get(main) + 1);  // 메인 근무 횟수 증가
                subDutyCounts.put(sub, subDutyCounts.get(sub) + 1);      // 서브 근무 횟수 증가
            } else if (isWeekend(day, nextMonth)) {
                // 주말 메인 담당자 배정
                String main = assignDuty(members, day, schedule, vacationDays, weekendDutyCounts, lastDutyDay, mainDutyCounts, subDutyCounts, true, null, day - 1);
                if (main == null) continue;

                // 주말 서브 담당자 배정
                String sub = assignDuty(members, day, schedule, vacationDays, weekendDutyCounts, lastDutyDay, mainDutyCounts, subDutyCounts, false, main, day - 1);
                if (sub == null) continue;

                schedule.add(new Schedule(day, main, sub));
                lastDutyDay.put(main, day);
                lastDutyDay.put(sub, day);
                weekendDutyCounts.put(main, weekendDutyCounts.get(main) + 1);
                weekendDutyCounts.put(sub, weekendDutyCounts.get(sub) + 1);
                dutyCounts.put(main, dutyCounts.get(main) + 1);
                dutyCounts.put(sub, dutyCounts.get(sub) + 1);
                mainDutyCounts.put(main, mainDutyCounts.get(main) + 1);  // 메인 근무 횟수 증가
                subDutyCounts.put(sub, subDutyCounts.get(sub) + 1);      // 서브 근무 횟수 증가
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
                subDutyCounts.put(sub, subDutyCounts.get(sub) + 1);      // 서브 근무 횟수 증가
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

    // 연속 근무 여부 확인 (모든 요일에 대해 적용)
    private static boolean hasRecentDuty(List<Schedule> schedule, String member, int day, Map<String, Integer> lastDutyDay, int... checkDays) {
        int lastDay = lastDutyDay.getOrDefault(member, -1);
        // 최근 근무일과 하루 차이일 경우 연속 근무 금지
        if (lastDay != -1 && (day - lastDay) <= 1) {
            return true;
        }

        // 이전 근무일에 이미 근무한 경우 연속 근무 방지
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

    // 주말 확인 (토요일, 일요일 포함)
    private static boolean isWeekend(int day, YearMonth month) {
        LocalDate date = month.atDay(day);
        return date.getDayOfWeek().getValue() == 6 || date.getDayOfWeek().getValue() == 7;
    }

    // 금요일 확인
    private static boolean isFriday(int day, YearMonth month) {
        LocalDate date = month.atDay(day);
        return date.getDayOfWeek().getValue() == 5;
    }

    // 토요일 확인
    private static boolean isSaturday(int day, YearMonth month) {
        LocalDate date = month.atDay(day);
        return date.getDayOfWeek().getValue() == 6;
    }

    // 휴가 여부 확인
    private static boolean isOnVacation(Map<Integer, List<String>> vacationDays, String member, int day) {
        return vacationDays.containsKey(day) && vacationDays.get(day).contains(member);
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