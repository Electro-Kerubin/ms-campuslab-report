package org.campuslab.report.dto;

public record BookingResourcePayload(Long resourceId, Integer quantity) {
    public int effectiveQuantity() {
        return quantity == null ? 1 : quantity;
    }
}