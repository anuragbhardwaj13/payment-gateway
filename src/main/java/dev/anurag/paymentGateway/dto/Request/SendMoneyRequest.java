package dev.anurag.paymentGateway.dto.Request;

import lombok.Data;

@Data
public class SendMoneyRequest {
    private Long toAccountId;
    private Long amount;
    private String description;
}