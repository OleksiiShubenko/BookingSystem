package com.booking.dataModel.dto;

import com.booking.dataModel.PaymentStatus;

import java.time.Instant;

public record PaymentDetails(
        String transactionId,
        double cost,
        PaymentStatus status,
        Instant createdAt,
        String description
) {
}
