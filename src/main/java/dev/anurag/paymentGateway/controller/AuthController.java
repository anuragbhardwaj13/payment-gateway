package dev.anurag.paymentGateway.controller;

import dev.anurag.paymentGateway.dto.Request.AuthenticationRequest;
import dev.anurag.paymentGateway.dto.Response.AuthenticationResponse;
import dev.anurag.paymentGateway.dto.Request.RegisterRequest;
import dev.anurag.paymentGateway.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authService;

    @PostMapping("/signup")
    public ResponseEntity<AuthenticationResponse> signup(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/signin")
    public ResponseEntity<AuthenticationResponse> signin(@RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(authService.authenticate(request));
    }
}