package com.skillbridge.user.controller;
import com.skillbridge.user.dto.AvailabilityRequest;
import com.skillbridge.user.dto.AvailabilityResponse;
import com.skillbridge.user.service.MentorAvailabilityService;
import java.util.List;
import com.skillbridge.user.dto.*;
import com.skillbridge.user.service.MentorProfileService;
import com.skillbridge.user.service.UserProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserProfileController {

    private final MentorAvailabilityService availabilityService;
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

    // Availability endpoints

    @PostMapping("/{userId}/availability")
    public ResponseEntity<AvailabilityResponse> addAvailability(
            @PathVariable Long userId,
            @Valid @RequestBody AvailabilityRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(availabilityService.addAvailability(userId, request));
    }

    @GetMapping("/{userId}/availability")
    public ResponseEntity<List<AvailabilityResponse>> getAvailability(@PathVariable Long userId) {
        return ResponseEntity.ok(availabilityService.getAvailability(userId));
    }

    @DeleteMapping("/{userId}/availability/{slotId}")
    public ResponseEntity<Void> deleteAvailability(
            @PathVariable Long userId,
            @PathVariable Long slotId) {
        availabilityService.deleteAvailability(userId, slotId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Internal endpoint for review-service Feign calls.
     * Updates the mentor's average rating after a new review is submitted.
     *
     * This path is whitelisted in SecurityConfig — no JWT required.
     * It is only reachable from within the Docker network (not via API Gateway).
     */
    @PutMapping("/internal/{userId}/mentor-rating")
    public ResponseEntity<Void> updateMentorRating(
            @PathVariable Long userId,
            @RequestBody com.skillbridge.user.dto.UpdateRatingRequest request) {
        mentorProfileService.updateMentorRating(
                userId,
                request.getAverageRating(),
                request.getTotalReviews());
        return ResponseEntity.noContent().build();
    }
}