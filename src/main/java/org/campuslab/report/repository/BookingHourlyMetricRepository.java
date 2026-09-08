package org.campuslab.report.repository;

import org.campuslab.report.entity.BookingHourlyMetric;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingHourlyMetricRepository extends JpaRepository<BookingHourlyMetric, Long> {
    Optional<BookingHourlyMetric> findByLabIdAndBucketHour(Long labId, ZonedDateTime bucketHour);
    List<BookingHourlyMetric> findByBucketHourAfterOrderByBucketHourAsc(ZonedDateTime timestamp);
}