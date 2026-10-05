package org.campuslab.report.dto;

import org.campuslab.report.domain.model.BookingEventFact;

import java.time.ZonedDateTime;

public record UsageResponse(
        Long bookingId,
        String userId,
        String userEmail,
        String userName,
        Long labId,
        String labName,
        String status,
        ZonedDateTime occurredAt,
        ZonedDateTime cycleStart,
        ZonedDateTime cycleEnd,
        Long cycleMinutes) {
    public static UsageResponse from(BookingEventFact fact) {
        return new UsageResponse(fact.getBookingId(), fact.getUserId(), fact.getUserEmail(), fact.getUserName(),
                fact.getLabId(), fact.getLabName(), fact.getStatus(), fact.getOccurredAt(), null, null, null);
    }
}