package dev.anurag.paymentGateway.service;

import dev.anurag.paymentGateway.dto.Request.RegisterRequest;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Random;

@Service
public class VerificationService {
    private final Map<String, String> verificationCodes = new ConcurrentHashMap<>();
    private final Map<String, RegisterRequest> registrationRequests = new ConcurrentHashMap<>();

    public String generateVerificationCode(RegisterRequest request) {
        String code = String.format("%06d", new Random().nextInt(999999));
        verificationCodes.put(request.getEmail(), code);
        registrationRequests.put(request.getEmail(), request);
        return code;
    }

    public void saveVerificationCode(String email, String code) {
        verificationCodes.put(email, code);
    }

    public boolean verifyCode(String email, String code) {
        return code.equals(verificationCodes.get(email));
    }

    public void saveRegistrationRequest(String email, RegisterRequest request) {
        registrationRequests.put(email, request);
    }

    public RegisterRequest getRegistrationRequest(String email) {
        return registrationRequests.get(email);
    }
}