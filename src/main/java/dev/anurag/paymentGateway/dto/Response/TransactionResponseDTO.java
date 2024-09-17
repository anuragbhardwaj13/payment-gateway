package dev.anurag.paymentGateway.dto.Response;

import dev.anurag.paymentGateway.entity.Account;
import dev.anurag.paymentGateway.entity.Transaction;
import dev.anurag.paymentGateway.enums.TransactionStatus;
import dev.anurag.paymentGateway.enums.TransactionType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TransactionResponseDTO {
    private Long id;
    private String transactionType;
    private Long amount;
    private Long fromAccountId;
    private Long toAccountId;
    private String fromAccountNum;
    private String toAccountNum;
    private String status;
    private String description;
    private LocalDateTime createdAt;

    public static TransactionResponseDTO fromTransaction(Transaction transaction, Long viewerAccountId) {
        TransactionResponseDTO dto = new TransactionResponseDTO();
        dto.setId(transaction.getId());
        dto.setAmount(transaction.getAmount());
        dto.setFromAccountId(transaction.getFromAccount() != null ? transaction.getFromAccount().getId() : null);
        dto.setToAccountId(transaction.getToAccount() != null ? transaction.getToAccount().getId() : null);
        dto.setStatus(transaction.getStatus().toString());
        dto.setDescription(transaction.getDescription());
        dto.setCreatedAt(transaction.getCreatedAt());
        dto.setFromAccountNum(transaction.getFromAccount() != null ? transaction.getFromAccount().getAccountNum() : null);
        dto.setToAccountNum(transaction.getToAccount() != null ? transaction.getToAccount().getAccountNum() : null);

        if (transaction.getStatus() == TransactionStatus.FAILED) {
            dto.setTransactionType("Failed");
        } else if (transaction.getTransactionType() == TransactionType.DEPOSIT) {
            dto.setTransactionType("Credit");
        } else if (transaction.getTransactionType() == TransactionType.WITHDRAWAL) {
            dto.setTransactionType("Debit");
        } else if (transaction.getTransactionType() == TransactionType.TRANSFER) {
            if (viewerAccountId.equals(dto.getFromAccountId())) {
                dto.setTransactionType("Debit");
            } else {
                dto.setTransactionType("Credit");
            }
        }

        return dto;
    }
}