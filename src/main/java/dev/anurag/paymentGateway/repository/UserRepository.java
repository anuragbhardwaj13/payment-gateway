package dev.anurag.paymentGateway.repository;

import dev.anurag.paymentGateway.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.accounts WHERE u.id = :userId")
    Optional<User> findUserWithAccounts(@Param("userId") Long userId);
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
}