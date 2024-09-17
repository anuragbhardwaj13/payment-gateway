package dev.anurag.paymentGateway.controller;

import dev.anurag.paymentGateway.dto.Request.AddMoneyRequest;
import dev.anurag.paymentGateway.dto.Request.SendMoneyRequest;
import dev.anurag.paymentGateway.dto.Response.AccountResponse;
import dev.anurag.paymentGateway.dto.Response.TransactionResponseDTO;
import dev.anurag.paymentGateway.dto.Response.UserDTO;
import dev.anurag.paymentGateway.entity.Account;
import dev.anurag.paymentGateway.entity.Transaction;
import dev.anurag.paymentGateway.entity.User;
import dev.anurag.paymentGateway.enums.TransactionStatus;
import dev.anurag.paymentGateway.service.AccountService;
import dev.anurag.paymentGateway.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/account")
public class AccountController {
    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);
    private final AccountService accountService;
    private final UserService userService;

    @Autowired
    public AccountController(AccountService accountService,UserService userService) {
        this.accountService = accountService;
        this.userService = userService;
    }

    @GetMapping("/create-new")
    public ResponseEntity<AccountResponse> createNewAccount() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            User currentUser = (User) authentication.getPrincipal();

            Account newAccount = accountService.createAccount(currentUser.getId());
            AccountResponse responseDTO = AccountResponse.fromAccount(newAccount);
            return ResponseEntity.ok(responseDTO);
        }
        return ResponseEntity.badRequest().build();
    }
    @PostMapping("/{accountId}/add-money")
    public ResponseEntity<AccountResponse> addMoneyToAccount(@PathVariable Long accountId, @RequestBody AddMoneyRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            User currentUser = (User) authentication.getPrincipal();
            // You might want to add a check here to ensure the account belongs to the current user
            Transaction transaction = accountService.addMoneyToAccount(accountId, request.getAmount());
            Account updatedAccount = transaction.getToAccount();
            AccountResponse responseDTO = AccountResponse.fromAccount(updatedAccount);
            return ResponseEntity.ok(responseDTO);
        }
        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/{fromAccountId}/send-money")
    public ResponseEntity<TransactionResponseDTO> sendMoney(
            @PathVariable Long fromAccountId,
            @RequestBody SendMoneyRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            User currentUser = (User) authentication.getPrincipal();
            List<Long> userAccountIds = userService.getUserAccountIds(currentUser.getId());

            if (!userAccountIds.contains(fromAccountId)) {
                new RuntimeException("Source account not found with id: " + fromAccountId);
            }

            Transaction transaction = accountService.sendMoney(fromAccountId, request.getToAccountId(), request.getAmount(), request.getDescription());
            TransactionResponseDTO responseDTO = TransactionResponseDTO.fromTransaction(transaction, fromAccountId);

            if (transaction.getStatus() == TransactionStatus.FAILED) {
                return ResponseEntity.badRequest().body(responseDTO);
            }

            return ResponseEntity.ok(responseDTO);
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/{accountId}/transactions")
    public ResponseEntity<Page<TransactionResponseDTO>> getTransactions(
            @PathVariable Long accountId,
            Pageable pageable) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            User currentUser = (User) authentication.getPrincipal();
            Page<Transaction> transactions = accountService.getTransactionsForAccount(accountId, currentUser.getId(), pageable);
            Page<TransactionResponseDTO> transactionDTOs = transactions.map(
                    transaction -> TransactionResponseDTO.fromTransaction(transaction, accountId)
            );
            return ResponseEntity.ok(transactionDTOs);
        }
        return ResponseEntity.badRequest().build();
    }

}
