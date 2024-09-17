package dev.anurag.paymentGateway.service;

import dev.anurag.paymentGateway.dto.Response.AccountDTO;
import dev.anurag.paymentGateway.dto.Response.UserAccountsResponseDTO;
import dev.anurag.paymentGateway.dto.Response.UserDTO;
import dev.anurag.paymentGateway.entity.Account;
import dev.anurag.paymentGateway.entity.User;
import dev.anurag.paymentGateway.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public UserAccountsResponseDTO getUserAccountsInfo(Long userId) {
        User user = userRepository.findUserWithAccounts(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        List<AccountDTO> accountDTOs = user.getAccounts().stream()
                .map(this::convertToAccountDTO)
                .collect(Collectors.toList());

        Long totalBalance = accountDTOs.stream()
                .mapToLong(AccountDTO::getBalance)
                .sum();

        return UserAccountsResponseDTO.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .totalAccounts(accountDTOs.size())
                .totalBalance(totalBalance)
                .accounts(accountDTOs)
                .build();
    }

    private AccountDTO convertToAccountDTO(Account account) {
        return AccountDTO.builder()
                .id(account.getId())
                .accountNum(account.getAccountNum())
                .balance(account.getBalance())
                .build();
    }
    @Transactional(readOnly = true)
    public UserDTO getUserWithAccounts(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        return UserDTO.fromUser(user);
    }
    @Transactional(readOnly = true)
    public List<Long> getUserAccountIds(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        return user.getAccounts().stream()
                .map(Account::getId)
                .collect(Collectors.toList());
    }
}