package org.campuslab.report.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Entity
@Table(name = "booking_hourly_metrics", uniqueConstraints = @UniqueConstraint(
        name = "uq_booking_hourly_metrics_lab_bucket",
        columnNames = {"lab_id", "bucket_hour"}))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingHourlyMetric {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "lab_id", nullable = false)
    private Long labId;

    @Column(name = "bucket_hour", nullable = false)
    private ZonedDateTime bucketHour;

    @Builder.Default
    @Column(name = "bookings_count", nullable = false)
    private Integer bookingsCount = 0;

    @Column(name = "avg_cycle_minutes", precision = 10, scale = 2)
    private BigDecimal avgCycleMinutes;
}