package com.booking.controller;

import com.booking.dataModel.exceptions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class BookingControllerAdviceTest {

    private BookingControllerAdvice bookingControllerAdvice;

    @BeforeEach
    void setUp() {
        bookingControllerAdvice = new BookingControllerAdvice();
    }

    @Test
    void handleBookingOverlappingException_ShouldReturnBadRequest() {
        ResponseEntity<ErrorResponse> response = bookingControllerAdvice.handleGeneralException(new BookingException("Booking conflict"));

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(400, response.getBody().code());
        assertEquals("Booking conflict", response.getBody().message());
    }
    @Test
    void handlePaymentProcessException_ShouldReturnBadRequest() {
        ResponseEntity<ErrorResponse> response = bookingControllerAdvice.handleGeneralException(new PaymentProcessException("Booking conflict"));

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(400, response.getBody().code());
        assertEquals("Booking conflict", response.getBody().message());
    }

    @Test
    void handleEntityExistException_ShouldReturnBadRequest() {
        ResponseEntity<ErrorResponse> response = bookingControllerAdvice.handleEntityExistException(new EntityExistException("Entity already exists"));

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(400, response.getBody().code());
        assertEquals("Entity already exists", response.getBody().message());
    }

    @Test
    void handleEntityDoesNotExistException_ShouldReturnBadRequest() {
        ResponseEntity<ErrorResponse> response = bookingControllerAdvice.handleNotFoundException(new EntityNotFoundException("Entity already exists"));

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(404, response.getBody().code());
        assertEquals("Entity already exists", response.getBody().message());
    }

    @Test
    void handleNotFoundException_ShouldReturnNotFound() {
        ResponseEntity<ErrorResponse> response = bookingControllerAdvice.handleNotFoundException(new PaymentNotFoundException("Payment not found"));

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(404, response.getBody().code());
        assertEquals("Payment not found", response.getBody().message());
    }
}
