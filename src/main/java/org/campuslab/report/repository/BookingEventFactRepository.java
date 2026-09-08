package org.campuslab.report.repository;

import org.campuslab.report.entity.BookingEventFact;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingEventFactRepository extends JpaRepository<BookingEventFact, Long> {
    boolean existsByEventId(String eventId);
}