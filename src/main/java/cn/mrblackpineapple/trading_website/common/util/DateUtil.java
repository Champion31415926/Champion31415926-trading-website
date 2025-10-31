package cn.mrblackpineapple.trading_website.common.util;

import cn.mrblackpineapple.trading_website.dto.Request;
import cn.mrblackpineapple.trading_website.vo.ChartVO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DateUtil {

    public static Request startAndEndTime(Integer days) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextDayStart = now.minusDays(days).plusDays(1).with(LocalTime.of(0, 0));
        LocalDateTime daysAgoEnd = nextDayStart.minusSeconds(1);
        return Request.builder().startTime(daysAgoEnd).endTime(now).build();
    }

    public static List<ChartVO> countDatesWithinRange(Integer dayRange, List<LocalDateTime> dates) {
        Map<LocalDate, Long> dateCounts = dates.stream()
                .map(LocalDateTime::toLocalDate)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        LocalDate startDate = LocalDate.now().minusDays(dayRange);
        return IntStream.rangeClosed(0, dayRange)
                .mapToObj(startDate::plusDays)
                .filter(dateCounts::containsKey)
                .map(date -> new ChartVO(
                        String.format("%02d-%02d", date.getMonthValue(), date.getDayOfMonth()),
                        dateCounts.getOrDefault(date, 0L).intValue()))
                .collect(Collectors.toList());
    }
}