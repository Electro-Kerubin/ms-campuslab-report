package org.campuslab.report.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BookingResourcePayload(Long resourceId, Integer quantity) {
    public int effectiveQuantity() {
        return quantity == null ? 1 : quantity;
    }
}