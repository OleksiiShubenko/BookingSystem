package com.booking.controller;

import com.booking.dataModel.dto.PaymentDetails;
import com.booking.dataModel.dto.PaymentDto;
import com.booking.dataModel.exceptions.ErrorResponse;
import com.booking.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/payment")
@Tag(name = "Payment API", description = "Payment operations")
public class PaymentController {

    private final PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Operation(
            summary = "Process a payment",
            description = "Processes a payment based on the provided payment details.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Payment processed successfully",
                            content = @Content(schema = @Schema(implementation = PaymentDetails.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @PostMapping(value = "/process")
    public PaymentDetails paymentProcessing(@RequestBody PaymentDto paymentDto) {
        return paymentService.processPayment(paymentDto);
    }

}
