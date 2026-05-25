package com.skillbridge.booking.client;

import com.skillbridge.booking.client.dto.MentorProfileResponse;
import com.skillbridge.booking.client.dto.UserProfileResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", url = "${user.service.url:http://localhost:8082}")
public interface UserServiceClient {

    @GetMapping("/api/users/{userId}/profile")
    UserProfileResponse getUserProfile(@PathVariable Long userId);

    @GetMapping("/api/users/{userId}/mentor-profile")
    MentorProfileResponse getMentorProfile(@PathVariable Long userId);
}
