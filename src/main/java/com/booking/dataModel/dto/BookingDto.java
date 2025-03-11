package com.booking.dataModel.dto;

import java.time.Instant;

public record BookingDto(
        String username,
        Integer unitId,
        Instant fromTime,
        Instant toTime
) {
}
