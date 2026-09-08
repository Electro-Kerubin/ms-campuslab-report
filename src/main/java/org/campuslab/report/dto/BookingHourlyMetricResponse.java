package org.campuslab.report.dto;

import org.campuslab.report.entity.BookingHourlyMetric;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public record BookingHourlyMetricResponse(
        Long labId,
        ZonedDateTime bucketHour,
        Integer bookingsCount,
        BigDecimal avgCycleMinutes) {
    public static BookingHourlyMetricResponse from(BookingHourlyMetric metric) {
        return new BookingHourlyMetricResponse(metric.getLabId(), metric.getBucketHour(),
                metric.getBookingsCount(), metric.getAvgCycleMinutes());
    }
}