package org.campuslab.report.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "booking_event_resources")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingEventResource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "booking_event_fact_id", nullable = false)
    private BookingEventFact bookingEventFact;

    @Column(name = "resource_id", nullable = false)
    private Long resourceId;

    @Builder.Default
    @Column(nullable = false)
    private Integer quantity = 1;
}