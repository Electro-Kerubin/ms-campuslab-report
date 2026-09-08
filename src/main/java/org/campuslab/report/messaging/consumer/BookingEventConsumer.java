package org.campuslab.report.messaging.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.campuslab.report.dto.BookingEventEnvelope;
import org.campuslab.report.dto.BookingEventPayload;
import org.campuslab.report.service.KpiReportService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookingEventConsumer {
    private final ObjectMapper objectMapper;
    private final KpiReportService kpiReportService;

    @KafkaListener(topics = "bookings.events", groupId = "report-group")
    public void consume(String message) throws JsonProcessingException {
        JavaType envelopeType = objectMapper.getTypeFactory().constructParametricType(
                BookingEventEnvelope.class, BookingEventPayload.class);
        BookingEventEnvelope<BookingEventPayload> envelope = objectMapper.readValue(message, envelopeType);
        if (envelope.eventId() == null || envelope.timestamp() == null || envelope.payload() == null) {
            throw new IllegalArgumentException("El evento Kafka no contiene envelope o payload obligatorio");
        }
        kpiReportService.processBookingEvent(envelope.eventId(), envelope.timestamp(), envelope.payload());
    }
}