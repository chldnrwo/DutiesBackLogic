package alpha;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Stream;

public class 근무로직_0927 {

    public static void main(String[] args) {
        List<String> members = Arrays.asList("최병훈", "최욱재", "조민기", "전상현", "조용현", "김종환", "이솔님", "신선규");

        // 현재 날짜를 기준으로 다음 달의 월을 가져옴
        LocalDate currentDate = LocalDate.now().plusMonths(1);
        //LocalDate currentDate = LocalDate.now();
        YearMonth nextMonth = YearMonth.of(currentDate.getYear(), currentDate.getMonth());
        int daysInMonth = nextMonth.lengthOfMonth();


        // 당월 휴가 목록
        List<Vacation> vacationSchedule = Arrays.asList(
            // 전월 말일 근무자
        	new Vacation(1, 1, "신선규"),
            new Vacation(1, 1, "조용현"),
            
            //휴가자
            new Vacation(4, 6, "조용현"),
            new Vacation(4, 6, "최욱재"),
            
            new Vacation(7, 7, "최병훈"),
            
            new Vacation(8, 8, "전상현"),
            
            new Vacation(10, 13, "신선규"),
            new Vacation(11, 13, "조민기"),
            
            new Vacation(12, 14, "이솔님"),
            new Vacation(12, 14, "조용현"),
            
            new Vacation(16, 16, "조민기"),
            
            new Vacation(18, 20, "최병훈"),
            new Vacation(18, 20, "최욱재"),
            
            new Vacation(25, 27, "조용현"),
            new Vacation(25, 27, "최욱재")
        );
        
        Map<String, Integer> dutyCounts = new HashMap<>();
        Map<String, Integer> weekendDutyCounts = new HashMap<>(); // 일요일 공휴일 근무 횟수
        Map<String, Integer> fridayDutyCounts = new HashMap<>(); // 금요일 근무 횟수
        Map<String, Integer> saturdayDutyCounts = new HashMap<>();
        Map<String, Integer> lastDutyDay = new HashMap<>();		// 최종 투입한지 얼마나 지났는지
        Map<String, Integer> mainDutyCounts = new HashMap<>();  // 메인 근무 횟수 카운트
        Map<String, Integer> subDutyCounts = new HashMap<>();   // 서브 근무 횟수 카운트
        for (String member : members) {
            dutyCounts.put(member, 0);
            weekendDutyCounts.put(member, 0);
            fridayDutyCounts.put(member, 0); // 금요일 근무 횟수 초기화
            saturdayDutyCounts.put(member, 0);
            lastDutyDay.put(member, -1);
            mainDutyCounts.put(member, 0);
            subDutyCounts.put(member, 0);
        }
        
        //9월 근무 수동입력
        dutyCounts.put("최병훈", 7);
        weekendDutyCounts.put("최병훈", 1);
        fridayDutyCounts.put("최병훈", 1); 
        saturdayDutyCounts.put("최병훈", 1);
        lastDutyDay.put("최병훈", 25);
        mainDutyCounts.put("최병훈", 3);
        subDutyCounts.put("최병훈", 4);
       
        dutyCounts.put("최욱재", 7);
        weekendDutyCounts.put("최욱재", 1);
        fridayDutyCounts.put("최욱재", 0); 
        saturdayDutyCounts.put("최욱재", 2);
        lastDutyDay.put("최욱재", 28);
        mainDutyCounts.put("최욱재", 4);
        subDutyCounts.put("최욱재", 3);
        
        dutyCounts.put("조민기", 8);
        weekendDutyCounts.put("조민기", 2);
        fridayDutyCounts.put("조민기", 2);
        saturdayDutyCounts.put("조민기", 0);
        lastDutyDay.put("조민기", 29);
        mainDutyCounts.put("조민기", 4);
        subDutyCounts.put("조민기", 4);
        
        dutyCounts.put("전상현", 8);
        weekendDutyCounts.put("전상현", 1);
        fridayDutyCounts.put("전상현", 2);
        saturdayDutyCounts.put("전상현", 1);
        lastDutyDay.put("전상현", 27);
        mainDutyCounts.put("전상현", 4);
        subDutyCounts.put("전상현", 4);
        
        dutyCounts.put("조용현", 8);
        weekendDutyCounts.put("조용현", 1);
        fridayDutyCounts.put("조용현", 0);
        saturdayDutyCounts.put("조용현", 2);
        lastDutyDay.put("조용현", 30);
        mainDutyCounts.put("조용현", 4);
        subDutyCounts.put("조용현", 4);
        
        dutyCounts.put("김종환", 8);
        weekendDutyCounts.put("김종환", 2);
        fridayDutyCounts.put("김종환", 1);
        saturdayDutyCounts.put("김종환", 1);
        lastDutyDay.put("김종환", 28);
        mainDutyCounts.put("김종환", 4);
        subDutyCounts.put("김종환", 4);
        
        dutyCounts.put("이솔님", 8);
        weekendDutyCounts.put("이솔님", 1);
        fridayDutyCounts.put("이솔님", 1);
        saturdayDutyCounts.put("이솔님", 1);
        lastDutyDay.put("이솔님", 27);
        mainDutyCounts.put("이솔님", 4);
        subDutyCounts.put("이솔님", 4);
        
        dutyCounts.put("신선규", 8);
        weekendDutyCounts.put("신선규", 1);
        fridayDutyCounts.put("신선규", 1);
        saturdayDutyCounts.put("신선규", 1);
        lastDutyDay.put("신선규", 30);
        mainDutyCounts.put("신선규", 4);
        subDutyCounts.put("신선규", 4);
        
        //여기까지함
        
        // 메인 함수 호출
        List<Schedule> schedule = createSchedule(members, vacationSchedule, nextMonth,
        		daysInMonth, dutyCounts, weekendDutyCounts, fridayDutyCounts, lastDutyDay, mainDutyCounts, subDutyCounts, saturdayDutyCounts);

        // 스케줄 출력
        System.out.println("Schedule for " + nextMonth.getMonth() + " " + nextMonth.getYear());
        for (Schedule s : schedule) {
            System.out.println("Day " + s.day + ": Main: " + s.main + ", Sub: " + s.sub);
        }
        Map<String, Integer> weekdayCounts = new HashMap<>();  // 월화수목 근무 횟수
        Map<String, Integer> fridayCounts = new HashMap<>();   // 금요일 근무 횟수
        Map<String, Integer> saturdayCounts = new HashMap<>(); // 토요일 근무 횟수
        Map<String, Integer> sundayCounts = new HashMap<>();   // 일요일 및 공휴일 근무 횟수
        Map<String, Integer> mainDutyCountsCal = new HashMap<>();
        Map<String, Integer> subDutyCountsCal = new HashMap<>();
        // 초기화
        for (String member : members) {
            weekdayCounts.put(member, 0);
            fridayCounts.put(member, 0);
            saturdayCounts.put(member, 0);
            sundayCounts.put(member, 0);
            // mainDutyCounts와 subDutyCounts는 이미 선언되었으므로 초기화만 수행
            mainDutyCountsCal.put(member, 0);
            subDutyCountsCal.put(member, 0);
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
            mainDutyCountsCal.put(s.main, mainDutyCountsCal.get(s.main) + 1);
            subDutyCountsCal.put(s.sub, subDutyCountsCal.get(s.sub) + 1);
        }
        
        // 근무 통계 출력
        System.out.println("\nOctober Member-wise Duty Distribution:");
        System.out.println("Member | Weekday Duties | Friday Duties | Saturday Duties | Sunday/Holiday Duties | Total Duties | Main Duties | Sub Duties");
        System.out.println("---------------------------------------------------------------------------------------------------------------------------");
        
        for (String member : members) {
            System.out.printf("%-4s | %-14d | %-13d | %-15d | %-22d | %-12d | %-11d | %-10d%n", 
                              member, 
                              weekdayCounts.get(member),      // 월화수목 근무 횟수
                              fridayCounts.get(member),       // 금요일 근무 횟수
                              saturdayCounts.get(member),     // 토요일 근무 횟수
                              sundayCounts.get(member),       // 일요일 및 공휴일 근무 횟수
                              (weekdayCounts.get(member)+fridayCounts.get(member)+saturdayCounts.get(member)+sundayCounts.get(member)),								  // 총 근무 횟수
                              mainDutyCountsCal.get(member),     // 메인 담당 횟수
                              subDutyCountsCal.get(member));     // 서브 담당 횟수
        }
        
        System.out.println();
        System.out.println("\nTotal Member-wise Duty Distribution( Since 2024/09 ~ ):");
        System.out.println("Member | Weekday Duties | Friday Duties | Saturday Duties | Sunday/Holiday Duties | Total Duties | Main Duties | Sub Duties");
        System.out.println("---------------------------------------------------------------------------------------------------------------------------");
        
        for (String member : members) {
            System.out.printf("%-4s | %-14d | %-13d | %-15d | %-22d | %-12d | %-11d | %-10d%n", 
                              member, 
                              dutyCounts.get(member)-fridayDutyCounts.get(member)-saturdayDutyCounts.get(member)-weekendDutyCounts.get(member),      // 월화수목 근무 횟수
                              fridayDutyCounts.get(member),       // 금요일 근무 횟수
                              saturdayDutyCounts.get(member),     // 토요일 근무 횟수
                              weekendDutyCounts.get(member),       // 일요일 및 공휴일 근무 횟수
                              dutyCounts.get(member),								  // 총 근무 횟수
                              mainDutyCounts.get(member),     // 메인 담당 횟수
                              subDutyCounts.get(member)      // 서브 담당 횟수
                              );     
            				  	
        }
        
    }

    // 스케줄 생성 함수
    public static List<Schedule> createSchedule(List<String> members, List<Vacation> vacationSchedule, YearMonth nextMonth, int daysInMonth,
                                                Map<String, Integer> dutyCounts, Map<String, Integer> weekendDutyCounts, 
                                                Map<String, Integer> fridayDutyCounts, Map<String, Integer> lastDutyDay, 
                                                Map<String, Integer> mainDutyCounts, Map<String, Integer> subDutyCounts, Map<String, Integer> saturdayDutyCounts) {
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
                String main = assignDuty(members, day, schedule, vacationDays, saturdayDutyCounts, lastDutyDay, mainDutyCounts, subDutyCounts, true, null, day - 1);
                if (main == null) continue;
               
                // 토요일 서브 담당자 배정
                String sub = assignDuty(members, day, schedule, vacationDays, saturdayDutyCounts, lastDutyDay, mainDutyCounts, subDutyCounts, false, main, day - 1);
                if (sub == null) continue;

                schedule.add(new Schedule(day, main, sub));
                lastDutyDay.put(main, day);
                lastDutyDay.put(sub, day);
                saturdayDutyCounts.put(main, saturdayDutyCounts.get(main) + 1);
                saturdayDutyCounts.put(sub, saturdayDutyCounts.get(sub) + 1);
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
        if (lastDay != -1 && ( 0<(day - lastDay) && (day - lastDay)<=1)) {
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
        if(YearMonth.of(2024, 10).equals(month) && (day==1 || day==3 || day==9)){
            return true;
         }
    	
        LocalDate date = month.atDay(day);
        return date.getDayOfWeek().getValue() == 7;
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