package com.booking.controller;

import com.booking.dataModel.PaymentStatus;
import com.booking.dataModel.dto.PaymentDetails;
import com.booking.dataModel.dto.PaymentDto;
import com.booking.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentControllerTest {

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private PaymentController paymentController;

    @Test
    void paymentProcessingDetails() {
        PaymentDto paymentDto = new PaymentDto("xxxx-xxxx-xxxx-0001", 285.5);
        PaymentDetails expectedDetails = new PaymentDetails("xxxx-xxxx-xxxx-0001", 285.5, PaymentStatus.PAID, Instant.now(), "Payment");
        when(paymentService.processPayment(paymentDto)).thenReturn(expectedDetails);

        PaymentDetails actualDetails = paymentController.paymentProcessing(paymentDto);

        assertNotNull(actualDetails);
        assertEquals(expectedDetails, actualDetails);
        verify(paymentService, times(1)).processPayment(paymentDto);
    }
}
