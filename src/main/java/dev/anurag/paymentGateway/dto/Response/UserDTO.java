package dev.anurag.paymentGateway.dto.Response;

import dev.anurag.paymentGateway.entity.Account;
import dev.anurag.paymentGateway.entity.User;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private List<Long> accountIds;

    public static UserDTO fromUser(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setAccountIds(user.getAccounts().stream()
                .map(Account::getId)
                .collect(Collectors.toList()));
        return dto;
    }
}