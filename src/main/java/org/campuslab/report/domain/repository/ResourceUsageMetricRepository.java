package org.campuslab.report.domain.repository;

import org.campuslab.report.domain.model.ResourceUsageMetric;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

public interface ResourceUsageMetricRepository extends JpaRepository<ResourceUsageMetric, Long> {
    Optional<ResourceUsageMetric> findByResourceIdAndPeriodStartAndPeriodEnd(
            Long resourceId, ZonedDateTime periodStart, ZonedDateTime periodEnd);

    List<ResourceUsageMetric> findByPeriodStartAfter(ZonedDateTime periodStart);
}