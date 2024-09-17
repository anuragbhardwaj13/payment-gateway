package dev.anurag.paymentGateway.dto.Response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserAccountsResponseDTO {
    private Long userId;
    private String username;
    private String fullName;
    private String email;
    private int totalAccounts;
    private Long totalBalance;
    private List<AccountDTO> accounts;
}

