package dev.anurag.paymentGateway.service;

import dev.anurag.paymentGateway.entity.Account;
import dev.anurag.paymentGateway.entity.Transaction;
import dev.anurag.paymentGateway.entity.User;
import dev.anurag.paymentGateway.enums.TransactionStatus;
import dev.anurag.paymentGateway.enums.TransactionType;
import dev.anurag.paymentGateway.repository.AccountRepository;
import dev.anurag.paymentGateway.repository.TransactionRepository;
import dev.anurag.paymentGateway.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.config.ConfigDataResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository, UserRepository userRepository,TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
        this.transactionRepository=transactionRepository;
    }

    public List<Account> findAccountsByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        return accountRepository.findByUserId(userId);
    }

    @Transactional
    public Account createAccount(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        if(user.getAccounts().size()>1){
            throw  new RuntimeException("User Already have an Account");
        }
        String accountNumber = generateUniqueAccountNumber(user.getEmail());

        Account newAccount = Account.builder()
                .user(user)
                .accountNum(accountNumber)
                .balance(0L)
                .build();

        return accountRepository.save(newAccount);
    }

    @Transactional
    public Transaction sendMoney(Long fromAccountId, Long toAccountId, Long amount, String description) {
        Account fromAccount = accountRepository.findById(fromAccountId)
                .orElseThrow(() -> new RuntimeException("Source account not found with id: " + fromAccountId));
        Account toAccount = accountRepository.findById(toAccountId)
                .orElseThrow(() -> new RuntimeException("Destination account not found with id: " + toAccountId));

        if (fromAccount.getBalance() < amount) {
            return createTransaction(TransactionType.TRANSFER, amount, fromAccount, null,
                    "Failed: Insufficient Balance: "+description, TransactionStatus.FAILED);
        }

        fromAccount.setBalance(fromAccount.getBalance() - amount);
        toAccount.setBalance(toAccount.getBalance() + amount);

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        return createTransaction(TransactionType.TRANSFER, amount, fromAccount, toAccount,
                "Transfer: " + description, TransactionStatus.COMPLETED);
    }

    private Transaction createTransaction(TransactionType type, Long amount, Account fromAccount, Account toAccount,
                                          String description, TransactionStatus status) {
        Transaction transaction = Transaction.builder()
                .transactionType(type)
                .amount(amount)
                .fromAccount(fromAccount)
                .toAccount(toAccount)
                .status(status)
                .description(description)
                .build();

        return transactionRepository.save(transaction);
    }

    @Transactional(readOnly = true)
    public Page<Transaction> getTransactionsForAccount(Long accountId, Long userId, Pageable pageable) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found with id: " + accountId));

        if (!account.getUser().getId().equals(userId)) {
            throw new RuntimeException("User does not have permission to access this account's transactions");
        }

        return transactionRepository.findTransactionsForAccount(accountId, pageable);
    }

    @Transactional
    public Transaction addMoneyToAccount(Long accountId, Long amount) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found with id: " + accountId));

        account.setBalance(account.getBalance() + amount);
        accountRepository.save(account);

        return createTransaction(TransactionType.DEPOSIT, amount, null, account, "Deposit: Money added to account",TransactionStatus.COMPLETED);
    }



    @Transactional(readOnly = true)
    public Long getAccountBalance(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found with id: " + accountId));
        return account.getBalance();
    }


    private String generateUniqueAccountNumber(String email) {
        try {
            // Use SHA-256 to hash the email
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(email.getBytes(StandardCharsets.UTF_8));

            // Convert the byte array to a hexadecimal string
            StringBuilder hexString = new StringBuilder(2 * encodedHash.length);
            for (byte b : encodedHash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            // Take the first 16 characters of the hash
            String accountNumber = hexString.toString().substring(0, 16).toUpperCase();

            // Check if this account number already exists
            while (accountRepository.existsByAccountNum(accountNumber)) {
                // If it exists, append a random digit and check again
                accountNumber = accountNumber.substring(1) + Integer.toString((int) (Math.random() * 10));
            }

            return accountNumber;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error generating account number", e);
        }
    }

}