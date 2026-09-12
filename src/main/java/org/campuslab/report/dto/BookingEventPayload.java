package org.campuslab.report.dto;

import java.util.List;

public record BookingEventPayload(
        Long bookingId,
        Long labId,
        String status,
        List<BookingResourcePayload> resources) {
}