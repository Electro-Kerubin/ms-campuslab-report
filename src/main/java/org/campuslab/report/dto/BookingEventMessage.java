package org.campuslab.report.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.ZonedDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BookingEventMessage(
        String eventId,
        ZonedDateTime timestamp,
        String type,
        String traceId,
        String correlationId,
        BookingEventPayload payload) {
}