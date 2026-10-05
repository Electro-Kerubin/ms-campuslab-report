package org.campuslab.report.service;

import org.junit.jupiter.api.Test;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PeriodResolverTest {
    private static final ZoneId SANTIAGO = ZoneId.of("America/Santiago");
    private final PeriodResolver resolver = new PeriodResolver("America/Santiago", 1095);
    private final ZonedDateTime now = ZonedDateTime.of(2026, 10, 5, 12, 0, 0, 0, SANTIAGO);

    @Test
    void resolvesSemesterBoundaries() {
        ResolvedPeriod period = resolver.resolve(null, null, null, "2026-S1", null, null, null, null, null, now);
        assertEquals("2026-01-01T00:00-03:00[America/Santiago]", period.from().toString());
        assertEquals("2026-07-01T00:00-04:00[America/Santiago]", period.to().toString());
    }

    @Test
    void resolvesLeapYearMonth() {
        ResolvedPeriod period = resolver.resolve(null, null, null, null, null, "2024-02", null, null, null, now);
        assertEquals(29, period.from().toLocalDate().lengthOfMonth());
        assertEquals(1, period.to().getDayOfMonth());
    }

    @Test
    void resolvesIsoWeek() {
        ResolvedPeriod period = resolver.resolve(null, null, null, null, null, null, null, "2026-W10", null, now);
        assertEquals("2026-03-02", period.from().toLocalDate().toString());
        assertEquals("2026-03-09", period.to().toLocalDate().toString());
    }

    @Test
    void rejectsMultipleSelectors() {
        assertThrows(IllegalArgumentException.class, () -> resolver.resolve(
                "last24h", null, null, "2026-S1", null, null, null, null, null, now));
    }
}