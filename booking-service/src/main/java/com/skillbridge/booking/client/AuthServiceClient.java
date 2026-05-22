package com.skillbridge.booking.client;

import com.skillbridge.booking.client.dto.UserEmailResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "auth-service", url = "${auth.service.url}")
public interface AuthServiceClient {

    @GetMapping("/api/auth/internal/users/{userId}")
    UserEmailResponse getUserById(@PathVariable Long userId);
}
