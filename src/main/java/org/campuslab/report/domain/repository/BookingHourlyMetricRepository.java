package org.campuslab.report.domain.repository;

import org.campuslab.report.domain.model.BookingHourlyMetric;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingHourlyMetricRepository extends JpaRepository<BookingHourlyMetric, Long> {
    Optional<BookingHourlyMetric> findByLabIdAndBucketHour(Long labId, ZonedDateTime bucketHour);

    List<BookingHourlyMetric> findByBucketHourAfter(ZonedDateTime timestamp);
}