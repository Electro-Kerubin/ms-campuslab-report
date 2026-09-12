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
@Table(name = "resource_usage_metrics", uniqueConstraints = @UniqueConstraint(
        name = "uq_resource_usage_metrics_resource_period",
        columnNames = {"resource_id", "period_start", "period_end"}))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResourceUsageMetric {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "resource_id", nullable = false)
    private Long resourceId;

    @Column(name = "period_start", nullable = false)
    private ZonedDateTime periodStart;

    @Column(name = "period_end", nullable = false)
    private ZonedDateTime periodEnd;

    @Builder.Default
    @Column(name = "usage_count", nullable = false)
    private Integer usageCount = 0;

    @Builder.Default
    @Column(name = "total_duration_minutes", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalDurationMinutes = BigDecimal.ZERO;
}