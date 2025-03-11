package com.booking.service;

import com.booking.dataModel.Booking;
import com.booking.dataModel.BookingStatus;
import com.booking.dataModel.Payment;
import com.booking.dataModel.PaymentStatus;
import com.booking.dataModel.dto.EventDto;
import com.booking.dataModel.dto.PaymentDetails;
import com.booking.dataModel.dto.PaymentDto;
import com.booking.dataModel.exceptions.PaymentNotFoundException;
import com.booking.dataModel.exceptions.PaymentProcessException;
import com.booking.kafka.KafkaEventProducer;
import com.booking.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private KafkaEventProducer kafkaEventProducer;

    @InjectMocks
    private PaymentService paymentService;

    private PaymentDto paymentDto;
    private Payment payment;

    @BeforeEach
    void setUp() {
        paymentDto = new PaymentDto("12345", 150.0);
        payment = Payment.builder()
                .transactionId("12345")
                .cost(150.0)
                .status(PaymentStatus.PENDING)
                .createdAt(Instant.now())
                .booking(Booking.builder().build())
                .build();
    }

    @Test
    void shouldProcessPaymentSuccessfully() {
        when(paymentRepository.findByTransactionId(paymentDto.transactionId())).thenReturn(payment);

        PaymentDetails paymentDetails = paymentService.processPayment(paymentDto);

        assertEquals(payment.getTransactionId(), paymentDetails.transactionId());
        assertEquals(payment.getCost(), paymentDetails.cost());
        assertEquals(PaymentStatus.PAID, paymentDetails.status());
        assertEquals("Funds are transferred, payment completed, booking are confirmed", paymentDetails.description());
        verify(paymentRepository).save(payment);
        verify(kafkaEventProducer).generateEvent(any(EventDto.class));
    }

    @Test
    void shouldThrowPaymentNotFoundExceptionWhenPaymentNotFound() {
        when(paymentRepository.findByTransactionId(paymentDto.transactionId())).thenReturn(null);

        PaymentNotFoundException exception = assertThrows(PaymentNotFoundException.class, () ->
                paymentService.processPayment(paymentDto));
        assertEquals("Payment with transaction id: 12345 is not found", exception.getMessage());
    }

    @Test
    void shouldThrowPaymentProcessExceptionWhenPaymentIsNotPending() {
        payment.setStatus(PaymentStatus.PAID);
        when(paymentRepository.findByTransactionId(paymentDto.transactionId())).thenReturn(payment);

        PaymentProcessException exception = assertThrows(PaymentProcessException.class, () ->
                paymentService.processPayment(paymentDto));
        assertEquals("Payment transaction with id: 12345 should have PENDING status to perform payment. Current status is: PAID", exception.getMessage());
    }

    @Test
    void shouldThrowPaymentProcessExceptionWhenFundsAreInsufficient() {
        payment.setCost(200.0);
        when(paymentRepository.findByTransactionId(paymentDto.transactionId())).thenReturn(payment);

        PaymentProcessException exception = assertThrows(PaymentProcessException.class, () ->
                paymentService.processPayment(paymentDto));
        assertEquals("Payment transaction with id: 12345 is failed: Not enough money: 12345 required money: 200.0", exception.getMessage());
    }

    @Test
    void shouldCancelPaymentSuccessfully() {
        when(paymentRepository.findPaymentWithBooking(paymentDto.transactionId())).thenReturn(payment);

        paymentService.cancelPayment(paymentDto.transactionId());

        assertEquals(PaymentStatus.CANCELLED, payment.getStatus());
        assertEquals(BookingStatus.CANCELLED, payment.getBooking().getStatus());
        verify(paymentRepository).save(payment);
    }

    @Test
    void shouldSetSuccessfulPayment() {
        when(paymentRepository.findByTransactionId(paymentDto.transactionId())).thenReturn(payment);

        paymentService.setSuccessfulPayment(paymentDto.transactionId());

        assertEquals(PaymentStatus.PAID, payment.getStatus());
        verify(paymentRepository).save(payment);
    }
}
