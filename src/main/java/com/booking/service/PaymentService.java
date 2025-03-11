package com.booking.service;

import com.booking.dataModel.BookingStatus;
import com.booking.dataModel.EventType;
import com.booking.dataModel.Payment;
import com.booking.dataModel.PaymentStatus;
import com.booking.dataModel.dto.EventDto;
import com.booking.dataModel.dto.PaymentDetails;
import com.booking.dataModel.dto.PaymentDto;
import com.booking.dataModel.exceptions.PaymentNotFoundException;
import com.booking.dataModel.exceptions.PaymentProcessException;
import com.booking.kafka.KafkaEventProducer;
import com.booking.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final KafkaEventProducer kafkaEventProducer;

    public PaymentService(PaymentRepository paymentRepository, KafkaEventProducer kafkaEventProducer) {
        this.paymentRepository = paymentRepository;
        this.kafkaEventProducer = kafkaEventProducer;
    }

    @Transactional
    public PaymentDetails processPayment(PaymentDto paymentDto) {
        Payment payment = paymentRepository.findByTransactionId(paymentDto.transactionId());
        if (payment == null) {
            throw new PaymentNotFoundException("Payment with transaction id: " + paymentDto.transactionId() + " is not found");
        } else if (!payment.getStatus().equals(PaymentStatus.PENDING)) {
            throw new PaymentProcessException(
                    "Payment transaction with id: " + payment.getTransactionId() + " should have PENDING status to perform payment. Current status is: " + payment.getStatus());
        } else if (paymentDto.funds() < payment.getCost()) {
            throw new PaymentProcessException(
                    "Payment transaction with id: " + payment.getTransactionId() + " is failed: Not enough money: " + paymentDto.transactionId() + " required money: " + payment.getCost());
        }

        //money are transferred
        payment.setStatus(PaymentStatus.PAID);
        paymentRepository.save(payment);

        kafkaEventProducer.generateEvent(new EventDto(payment.getTransactionId(), EventType.PAYMENT_SUCCESS));
        return new PaymentDetails(
                payment.getTransactionId(),
                payment.getCost(),
                payment.getStatus(),
                payment.getCreatedAt(),
                "Funds are transferred, payment completed, booking are confirmed"
        );
    }

    @Transactional
    public void cancelPayment(String transactionId) {
        var payment = paymentRepository.findPaymentWithBooking(transactionId);
        payment.setStatus(PaymentStatus.CANCELLED);
        payment.getBooking().setStatus(BookingStatus.CANCELLED);
        payment.getBooking().setCancelTime(Instant.now());

        paymentRepository.save(payment);
    }

    @Transactional
    public void setSuccessfulPayment(String transactionId) {
        var payment = paymentRepository.findByTransactionId(transactionId);
        payment.setStatus(PaymentStatus.PAID);

        paymentRepository.save(payment);
    }
}
