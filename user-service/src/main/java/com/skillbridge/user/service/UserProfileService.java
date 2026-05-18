package com.skillbridge.user.service;

import com.skillbridge.user.dto.*;
import com.skillbridge.user.model.UserProfile;
import com.skillbridge.user.model.Role;
import com.skillbridge.user.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;

    //  Create profile
    @Transactional
    public UserProfileResponse createProfile(Long userId, String role, CreateProfileRequest request) {

        if (userProfileRepository.existsByUserId(userId)) {
            throw new IllegalArgumentException("Profile already exists for this user");
        }

        UserProfile profile = UserProfile.builder()
                .userId(userId)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .bio(request.getBio())
                .profilePictureUrl(request.getProfilePictureUrl())
                .role(Role.valueOf(role))
                .build();

        userProfileRepository.save(profile);

        return toResponse(profile);
    }

    //  Get profile
    public UserProfileResponse getProfile(Long userId) {

        UserProfile profile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Profile not found"));

        return toResponse(profile);
    }

    //  Update profile
    @Transactional
    public UserProfileResponse updateProfile(Long userId, UpdateProfileRequest request) {

        UserProfile profile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Profile not found"));

        if (request.getFirstName() != null) profile.setFirstName(request.getFirstName());
        if (request.getLastName() != null) profile.setLastName(request.getLastName());
        if (request.getBio() != null) profile.setBio(request.getBio());
        if (request.getProfilePictureUrl() != null) profile.setProfilePictureUrl(request.getProfilePictureUrl());

        userProfileRepository.save(profile);

        return toResponse(profile);
    }

    // Mapper
    private UserProfileResponse toResponse(UserProfile profile) {
        return UserProfileResponse.builder()
                .id(profile.getId())
                .userId(profile.getUserId())
                .firstName(profile.getFirstName())
                .lastName(profile.getLastName())
                .bio(profile.getBio())
                .profilePictureUrl(profile.getProfilePictureUrl())
                .role(profile.getRole().name())
                .createdAt(profile.getCreatedAt())
                .build();
    }
}