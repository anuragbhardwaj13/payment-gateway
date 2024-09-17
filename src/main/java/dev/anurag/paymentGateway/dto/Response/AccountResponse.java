package dev.anurag.paymentGateway.dto.Response;

import dev.anurag.paymentGateway.entity.Account;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountResponse {
    private Long id;
    private String accountNum;
    private Long balance;
    private String userName;
    private Long userId;

    public static AccountResponse fromAccount(Account account) {
        return AccountResponse.builder()
                .id(account.getId())
                .accountNum(account.getAccountNum())
                .balance(account.getBalance())
                .userName(account.getUser().getFullName())
                .userId(account.getUser().getId())
                .build();
    }
}