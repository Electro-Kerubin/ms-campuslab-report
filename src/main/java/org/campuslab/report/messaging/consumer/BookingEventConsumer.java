package org.campuslab.report.messaging.consumer;

import lombok.RequiredArgsConstructor;
import org.campuslab.report.dto.BookingEventMessage;
import org.campuslab.report.dto.BookingEventPayload;
import org.campuslab.report.service.KpiReportService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookingEventConsumer {
    private final KpiReportService kpiReportService;

    @KafkaListener(topics = "${report.kafka.topic:bookings.events}")
    public void consume(BookingEventMessage event) {
        if (event == null || event.eventId() == null || event.timestamp() == null || event.payload() == null) {
            throw new IllegalArgumentException("El evento Kafka no contiene envelope o payload obligatorio");
        }
        BookingEventPayload payload = event.payload();
        if (payload.bookingId() == null || payload.labId() == null || payload.status() == null) {
            throw new IllegalArgumentException("El evento Kafka no contiene bookingId, labId o status obligatorio");
        }
        kpiReportService.processBookingEvent(event.eventId(), event.timestamp(), payload);
    }
}