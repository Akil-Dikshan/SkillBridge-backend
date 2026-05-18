package com.skillbridge.user.controller;

import com.skillbridge.user.dto.*;
import com.skillbridge.user.service.MentorProfileService;
import com.skillbridge.user.service.UserProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;
    private final MentorProfileService mentorProfileService;

    // User profile endpoints

    @PostMapping("/{userId}/profile")
    public ResponseEntity<UserProfileResponse> createProfile(
            @PathVariable Long userId,
            @RequestParam String role,
            @Valid @RequestBody CreateProfileRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userProfileService.createProfile(userId, role, request));
    }

    @GetMapping("/{userId}/profile")
    public ResponseEntity<UserProfileResponse> getProfile(@PathVariable Long userId) {
        return ResponseEntity.ok(userProfileService.getProfile(userId));
    }

    @PutMapping("/{userId}/profile")
    public ResponseEntity<UserProfileResponse> updateProfile(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(userProfileService.updateProfile(userId, request));
    }

    //  Mentor profile endpoints

    @PostMapping("/{userId}/mentor-profile")
    public ResponseEntity<MentorProfileResponse> createMentorProfile(
            @PathVariable Long userId,
            @Valid @RequestBody CreateMentorProfileRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mentorProfileService.createMentorProfile(userId, request));
    }

    @GetMapping("/{userId}/mentor-profile")
    public ResponseEntity<MentorProfileResponse> getMentorProfile(@PathVariable Long userId) {
        return ResponseEntity.ok(mentorProfileService.getMentorProfile(userId));
    }

    @PutMapping("/{userId}/mentor-profile")
    public ResponseEntity<MentorProfileResponse> updateMentorProfile(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateMentorProfileRequest request) {
        return ResponseEntity.ok(mentorProfileService.updateMentorProfile(userId, request));
    }
}