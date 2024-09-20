package dev.anurag.paymentGateway.controller;

import dev.anurag.paymentGateway.dto.Request.AuthenticationRequest;
import dev.anurag.paymentGateway.dto.Request.VerificationRequest;
import dev.anurag.paymentGateway.dto.Response.AuthenticationResponse;
import dev.anurag.paymentGateway.dto.Request.RegisterRequest;
import dev.anurag.paymentGateway.service.AuthenticationService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authService;

    @PostMapping("/signup/initiate")
    public ResponseEntity<?> initiateSignup(@RequestBody RegisterRequest request) throws MessagingException {
        authService.initiateRegistration(request);
        return ResponseEntity.ok("Verification code sent to email.");
    }

    @PostMapping("/signup/complete")
    public ResponseEntity<AuthenticationResponse> completeSignup(@RequestBody VerificationRequest request) {
        return ResponseEntity.ok(authService.completeRegistration(request.getEmail(), request.getVerificationCode()));
    }

    @PostMapping("/signin")
    public ResponseEntity<AuthenticationResponse> signin(@RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(authService.authenticate(request));
    }
}