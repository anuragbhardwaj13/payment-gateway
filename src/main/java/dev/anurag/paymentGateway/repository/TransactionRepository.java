package dev.anurag.paymentGateway.repository;

import dev.anurag.paymentGateway.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Page<Transaction> findByFromAccountIdOrToAccountId(Long fromAccountId, Long toAccountId, Pageable pageable);

    @Query("SELECT t FROM Transaction t WHERE t.fromAccount.id = :accountId OR t.toAccount.id = :accountId")
    Page<Transaction> findByAccountId(@Param("accountId") Long accountId, Pageable pageable);
    @Query("SELECT t FROM Transaction t WHERE " +
            "(t.fromAccount.id = :accountId) OR " +
            "(t.toAccount.id = :accountId AND t.status <> 'FAILED')")
    Page<Transaction> findTransactionsForAccount(@Param("accountId") Long accountId, Pageable pageable);
}