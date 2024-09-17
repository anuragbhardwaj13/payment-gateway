package dev.anurag.paymentGateway.controller;

import dev.anurag.paymentGateway.dto.Response.UserAccountsResponseDTO;
import dev.anurag.paymentGateway.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import dev.anurag.paymentGateway.entity.User;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }
    @GetMapping("/id")
    public ResponseEntity<?> getCurrentUserId() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            User currentUser = (User) authentication.getPrincipal();
            return ResponseEntity.ok(currentUser.getUsername());
        }
        return ResponseEntity.badRequest().build();
    }
    @GetMapping("/all-accounts")
    public ResponseEntity<?> getAllAccounts() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            User currentUser = (User) authentication.getPrincipal();
            UserAccountsResponseDTO response = userService.getUserAccountsInfo(currentUser.getId());
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().build();
    }
}