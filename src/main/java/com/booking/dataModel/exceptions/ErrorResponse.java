package com.booking.dataModel.exceptions;

public record ErrorResponse(
        Integer code,
        String message
) {
}
