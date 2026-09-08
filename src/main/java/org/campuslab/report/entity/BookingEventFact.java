package org.campuslab.report.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "booking_event_facts")
@Getter
@Setter
@NoArgsConstructor
public class BookingEventFact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id", nullable = false, unique = true, length = 64)
    private String eventId;

    @Column(name = "booking_id", nullable = false)
    private Long bookingId;

    @Column(name = "lab_id", nullable = false)
    private Long labId;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(name = "occurred_at", nullable = false)
    private ZonedDateTime occurredAt;

    @Column(name = "received_at", nullable = false)
    private ZonedDateTime receivedAt;

    @OneToMany(mappedBy = "bookingEventFact", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookingEventResource> resources = new ArrayList<>();

    public void addResource(BookingEventResource resource) {
        resources.add(resource);
        resource.setBookingEventFact(this);
    }
}