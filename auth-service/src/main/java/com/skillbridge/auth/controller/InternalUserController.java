package com.skillbridge.auth.controller;

import com.skillbridge.auth.dto.UserEmailResponse;
import com.skillbridge.auth.model.User;
import com.skillbridge.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth/internal")
@RequiredArgsConstructor
public class InternalUserController {

    private final UserRepository userRepository;

    /**
     * Internal endpoint for inter-service communication only.
     * Used by booking-service to look up a user's email by their ID
     * when publishing booking events to RabbitMQ.
     */
    @GetMapping("/users/{userId}")
    public ResponseEntity<UserEmailResponse> getUserById(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        return ResponseEntity.ok(UserEmailResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build());
    }
}
