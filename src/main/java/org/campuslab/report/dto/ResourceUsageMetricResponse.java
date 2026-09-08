package org.campuslab.report.dto;

import org.campuslab.report.entity.ResourceUsageMetric;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public record ResourceUsageMetricResponse(
        Long resourceId,
        ZonedDateTime periodStart,
        ZonedDateTime periodEnd,
        Integer usageCount,
        BigDecimal totalDurationMinutes) {
    public static ResourceUsageMetricResponse from(ResourceUsageMetric metric) {
        return new ResourceUsageMetricResponse(metric.getResourceId(), metric.getPeriodStart(),
                metric.getPeriodEnd(), metric.getUsageCount(), metric.getTotalDurationMinutes());
    }
}