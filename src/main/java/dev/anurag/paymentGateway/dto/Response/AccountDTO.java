package dev.anurag.paymentGateway.dto.Response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccountDTO {
    private Long id;
    private String accountNum;
    private Long balance;

}