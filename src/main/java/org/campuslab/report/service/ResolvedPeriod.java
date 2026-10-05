package org.campuslab.report.service;

import java.time.ZonedDateTime;

public record ResolvedPeriod(ZonedDateTime from, ZonedDateTime to) {
    public ResolvedPeriod {
        if (from == null || to == null || !from.isBefore(to)) {
            throw new IllegalArgumentException("El periodo debe cumplir from < to");
        }
    }
}