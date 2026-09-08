package org.campuslab.report.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Entity
@Table(name = "booking_hourly_metrics", uniqueConstraints = @UniqueConstraint(
        name = "uq_booking_hourly_metrics_lab_bucket", columnNames = {"lab_id", "bucket_hour"}))
@Getter
@Setter
@NoArgsConstructor
public class BookingHourlyMetric {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "lab_id", nullable = false)
    private Long labId;

    @Column(name = "bucket_hour", nullable = false)
    private ZonedDateTime bucketHour;

    @Column(name = "bookings_count", nullable = false)
    private Integer bookingsCount = 0;

    @Column(name = "avg_cycle_minutes", precision = 10, scale = 2)
    private BigDecimal avgCycleMinutes;
}