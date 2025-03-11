package com.booking.dataModel.exceptions;

public class PaymentProcessException extends RuntimeException {
    public PaymentProcessException(String message){
        super(message);
    }
}
