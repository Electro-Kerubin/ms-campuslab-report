package org.campuslab.report.domain.repository;

import org.campuslab.report.domain.model.BookingEventFact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingEventFactRepository extends JpaRepository<BookingEventFact, Long> {
    // Necesario para verificar la idempotencia al consumir mensajes de Kafka
    boolean existsByEventId(String eventId);
}