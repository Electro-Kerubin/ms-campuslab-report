package org.campuslab.report.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class PeriodResolver {
    private static final Pattern SEMESTER = Pattern.compile("(\\d{4})-S([12])");
    private static final Pattern ISO_WEEK = Pattern.compile("(\\d{4})-W(\\d{2})");
    private final ZoneId zoneId;
    private final long maxRangeDays;

    public PeriodResolver(@Value("${report.timezone:America/Santiago}") String timezone,
                          @Value("${report.max-range-days:1095}") long maxRangeDays) {
        this.zoneId = ZoneId.of(timezone);
        this.maxRangeDays = maxRangeDays;
    }

    public ResolvedPeriod resolve(String range, String from, String to, String semester,
                                  Integer lastSemesters, String month, Integer lastMonths,
                                  String week, Integer lastWeeks, ZonedDateTime now) {
        int selectors = countNonNull(range, from, semester, lastSemesters, month, lastMonths, week, lastWeeks);
        if (selectors > 1 || (from != null && to == null) || (from == null && to != null)) {
            throw invalid("Use exactamente un formato de periodo completo");
        }
        ZonedDateTime current = now == null ? ZonedDateTime.now(zoneId) : now.withZoneSameInstant(zoneId);
        ResolvedPeriod period;
        if (range != null) {
            period = relativeRange(range, current);
        } else if (from != null) {
            period = custom(from, to);
        } else if (semester != null) {
            period = semester(semester);
        } else if (lastSemesters != null) {
            period = lastSemesters(lastSemesters, current);
        } else if (month != null) {
            period = month(month);
        } else if (lastMonths != null) {
            period = lastMonths(lastMonths, current);
        } else if (week != null) {
            period = week(week);
        } else if (lastWeeks != null) {
            period = lastWeeks(lastWeeks, current);
        } else {
            period = relativeRange("last24h", current);
        }
        if (period.to().toLocalDate().toEpochDay() - period.from().toLocalDate().toEpochDay() > maxRangeDays) {
            throw invalid("El periodo supera el máximo permitido de " + maxRangeDays + " días");
        }
        return period;
    }

    private ResolvedPeriod relativeRange(String range, ZonedDateTime now) {
        return switch (range) {
            case "last24h" -> new ResolvedPeriod(now.minusHours(24), now);
            case "last7d" -> new ResolvedPeriod(now.minusDays(7), now);
            case "last30d" -> new ResolvedPeriod(now.minusDays(30), now);
            case "last90d" -> new ResolvedPeriod(now.minusDays(90), now);
            default -> throw invalid("range debe ser last24h, last7d, last30d o last90d");
        };
    }

    private ResolvedPeriod custom(String from, String to) {
        LocalDate start = parseDate(from, "from");
        LocalDate end = parseDate(to, "to");
        if (!start.isBefore(end)) throw invalid("from debe ser anterior a to");
        return dates(start, end);
    }

    private ResolvedPeriod semester(String value) {
        Matcher matcher = SEMESTER.matcher(value);
        if (!matcher.matches()) throw invalid("semester debe tener formato YYYY-S1 o YYYY-S2");
        int year = Integer.parseInt(matcher.group(1));
        LocalDate start = LocalDate.of(year, matcher.group(2).equals("1") ? 1 : 7, 1);
        return dates(start, start.plusMonths(6));
    }

    private ResolvedPeriod lastSemesters(int count, ZonedDateTime now) {
        if (count < 1 || count > 6) throw invalid("lastSemesters debe estar entre 1 y 6");
        LocalDate today = now.toLocalDate();
        int semesterStartMonth = today.getMonthValue() <= 6 ? 1 : 7;
        LocalDate end = LocalDate.of(today.getYear(), semesterStartMonth, 1);
        return dates(end.minusMonths(count * 6L), end);
    }

    private ResolvedPeriod month(String value) {
        try {
            YearMonth parsed = YearMonth.parse(value);
            return dates(parsed.atDay(1), parsed.plusMonths(1).atDay(1));
        } catch (RuntimeException exception) {
            throw invalid("month debe tener formato YYYY-MM");
        }
    }

    private ResolvedPeriod lastMonths(int count, ZonedDateTime now) {
        if (count < 1) throw invalid("lastMonths debe ser mayor que cero");
        YearMonth current = YearMonth.from(now);
        return dates(current.minusMonths(count - 1L).atDay(1), current.plusMonths(1).atDay(1));
    }

    private ResolvedPeriod week(String value) {
        Matcher matcher = ISO_WEEK.matcher(value);
        if (!matcher.matches()) throw invalid("week debe tener formato YYYY-Www");
        try {
            LocalDate date = LocalDate.of(Integer.parseInt(matcher.group(1)), Month.JANUARY, 4)
                    .with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, Integer.parseInt(matcher.group(2)))
                    .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            return dates(date, date.plusWeeks(1));
        } catch (RuntimeException exception) {
            throw invalid("week no es una semana ISO válida");
        }
    }

    private ResolvedPeriod lastWeeks(int count, ZonedDateTime now) {
        if (count < 1) throw invalid("lastWeeks debe ser mayor que cero");
        LocalDate end = now.toLocalDate().with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY));
        return dates(end.minusWeeks(count), end);
    }

    private ResolvedPeriod dates(LocalDate start, LocalDate end) {
        return new ResolvedPeriod(start.atStartOfDay(zoneId), end.atStartOfDay(zoneId));
    }

    private LocalDate parseDate(String value, String parameter) {
        try {
            return LocalDate.parse(value);
        } catch (RuntimeException exception) {
            throw invalid(parameter + " debe tener formato YYYY-MM-DD");
        }
    }

    private int countNonNull(Object... values) {
        int count = 0;
        for (Object value : values) if (value != null) count++;
        return count;
    }

    private IllegalArgumentException invalid(String message) {
        return new IllegalArgumentException(message);
    }
}