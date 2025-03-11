package com.booking.dataModel.dto;

public record SomePaymentDto(String transactionId, Double funds) {
}

//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//public class PaymentDto {
//    private String transactionId;
//    private Double funds;
//}