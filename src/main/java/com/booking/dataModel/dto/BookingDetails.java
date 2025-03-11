package com.booking.dataModel.dto;

import com.booking.dataModel.BookingStatus;

import java.time.Instant;

public record BookingDetails(
        String username,
        Integer unitId,
        Instant fromTime,
        Instant toTime,
        BookingStatus bookingStatus,
        PaymentDetails paymentDetails,
        String description
) {
}
