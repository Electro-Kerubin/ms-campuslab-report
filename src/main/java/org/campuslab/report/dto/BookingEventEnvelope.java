package org.campuslab.report.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.ZonedDateTime;

public record BookingEventEnvelope<T>(
        String eventId,
        ZonedDateTime timestamp,
        String type,
        @JsonProperty("payload") T payload) {
}