package org.campuslab.report.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BookingEventPayload(
        Long bookingId,
        Long labId,
        String status,
        List<BookingResourcePayload> resources,
        String userId,
        String userEmail,
        String userName,
        String labName) {
}