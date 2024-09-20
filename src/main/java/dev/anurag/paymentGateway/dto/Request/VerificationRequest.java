package dev.anurag.paymentGateway.dto.Request;

import lombok.Data;

@Data
public class VerificationRequest {
    private String email;
    private String verificationCode;
}