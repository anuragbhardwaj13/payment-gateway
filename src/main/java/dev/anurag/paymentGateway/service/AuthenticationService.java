package dev.anurag.paymentGateway.service;

import dev.anurag.paymentGateway.dto.Request.AuthenticationRequest;
import dev.anurag.paymentGateway.dto.Response.AuthenticationResponse;
import dev.anurag.paymentGateway.dto.Request.RegisterRequest;
import dev.anurag.paymentGateway.enums.Role;
import dev.anurag.paymentGateway.entity.User;
import dev.anurag.paymentGateway.exception.UserRegistrationException;
import dev.anurag.paymentGateway.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.dao.DataIntegrityViolationException;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {
        try {
            var user = User.builder()
                    .fullName(request.getFullName())
                    .username(request.getUsername())
                    .phone(request.getPhone())
                    .email(request.getEmail())
                    .governmentId(request.getGovernmentId())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .role(Role.USER)
                    .build();
            repository.save(user);
            var jwtToken = jwtService.generateToken(user);
            return AuthenticationResponse.builder()
                    .token(jwtToken)
                    .build();
        } catch (DataIntegrityViolationException e) {
            String message = "An error occurred during registration.";
            if (e.getCause() instanceof ConstraintViolationException) {
                ConstraintViolationException cve = (ConstraintViolationException) e.getCause();
                String constraintName = cve.getConstraintName();
                if (constraintName != null) {
                    if (constraintName.contains("phone")) {
                        message = "User with this phone number already exists.";
                    } else if (constraintName.contains("email")) {
                        message = "User with this email already exists.";
                    } else if (constraintName.contains("username")) {
                        message = "User with this username already exists.";
                    }
                }
            }
            throw new UserRegistrationException(message);
        }
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        logger.info("Attempting authentication for identifier: {}", request.getIdentifier());

        User user = repository.findByEmail(request.getIdentifier())
                .orElseGet(() -> repository.findByUsername(request.getIdentifier())
                        .orElseThrow(() -> new UsernameNotFoundException("User not found with identifier: " + request.getIdentifier())));

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getEmail(), // Use email for authentication
                        request.getPassword()
                )
        );

        var jwtToken = jwtService.generateToken(user);
        logger.info("Authentication successful for user: {}", user.getEmail());
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
}