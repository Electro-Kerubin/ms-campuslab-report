package org.campuslab.report.domain.repository;

import org.campuslab.report.domain.model.BookingEventFact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingEventFactRepository extends JpaRepository<BookingEventFact, Long> {
    // Necesario para verificar la idempotencia al consumir mensajes de Kafka
    boolean existsByEventId(String eventId);

        @Query("""
                        select fact from BookingEventFact fact
                        where fact.occurredAt >= :from and fact.occurredAt < :to
                            and (:userId is null or fact.userId = :userId)
                            and (:labId is null or fact.labId = :labId)
                            and (:status is null or fact.status = :status)
                        order by fact.occurredAt desc
                        """)
        Page<BookingEventFact> search(@Param("from") java.time.ZonedDateTime from,
                                                                    @Param("to") java.time.ZonedDateTime to,
                                                                    @Param("userId") String userId,
                                                                    @Param("labId") Long labId,
                                                                    @Param("status") String status,
                                                                    Pageable pageable);
}