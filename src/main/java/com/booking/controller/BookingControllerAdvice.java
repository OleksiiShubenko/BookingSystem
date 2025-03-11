package com.booking.controller;

import com.booking.dataModel.exceptions.*;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class BookingControllerAdvice {

    @ExceptionHandler({BookingException.class, PaymentProcessException.class})
    public ResponseEntity<ErrorResponse> handleGeneralException(RuntimeException exception){
        return new ResponseEntity<>(new ErrorResponse(400, exception.getMessage()), HttpStatusCode.valueOf(400));
    }

    @ExceptionHandler(EntityExistException.class)
    public ResponseEntity<ErrorResponse> handleEntityExistException(EntityExistException exception){
        return new ResponseEntity<>(new ErrorResponse(400, exception.getMessage()), HttpStatusCode.valueOf(400));
    }

    @ExceptionHandler({PaymentNotFoundException.class, EntityNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleNotFoundException(RuntimeException exception){
        return new ResponseEntity<>(new ErrorResponse(404, exception.getMessage()), HttpStatusCode.valueOf(404));
    }
}

