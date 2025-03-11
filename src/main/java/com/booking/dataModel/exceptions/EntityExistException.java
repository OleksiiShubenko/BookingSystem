package com.booking.dataModel.exceptions;

public class EntityExistException extends RuntimeException {
    public EntityExistException(String message){
        super(message);
    }
}