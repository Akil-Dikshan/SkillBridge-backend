package com.skillbridge.user.service;

import com.skillbridge.user.dto.CreateProfileRequest;
import com.skillbridge.user.dto.UpdateProfileRequest;
import com.skillbridge.user.dto.UserProfileResponse;
import com.skillbridge.user.model.Role;
import com.skillbridge.user.model.UserProfile;
import com.skillbridge.user.repository.UserProfileRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserProfileServiceTest {

    @Mock
    private UserProfileRepository userProfileRepository;

    @InjectMocks
    private UserProfileService userProfileService;

    @Test
    void createProfile_success() {
        when(userProfileRepository.existsByUserId(1L)).thenReturn(false);
        when(userProfileRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        CreateProfileRequest request = new CreateProfileRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setBio("Test bio");

        UserProfileResponse response = userProfileService.createProfile(1L, "STUDENT", request);

        assertThat(response.getFirstName()).isEqualTo("John");
        assertThat(response.getLastName()).isEqualTo("Doe");
        assertThat(response.getRole()).isEqualTo("STUDENT");
    }

    @Test
    void createProfile_throwsIfAlreadyExists() {
        when(userProfileRepository.existsByUserId(1L)).thenReturn(true);

        CreateProfileRequest request = new CreateProfileRequest();
        request.setFirstName("John");
        request.setLastName("Doe");

        assertThatThrownBy(() -> userProfileService.createProfile(1L, "STUDENT", request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Profile already exists for this user");
    }

    @Test
    void getProfile_returnsProfile() {
        UserProfile profile = UserProfile.builder()
                .id(1L)
                .userId(1L)
                .firstName("John")
                .lastName("Doe")
                .role(Role.STUDENT)
                .build();

        when(userProfileRepository.findByUserId(1L)).thenReturn(Optional.of(profile));

        UserProfileResponse response = userProfileService.getProfile(1L);

        assertThat(response.getFirstName()).isEqualTo("John");
        assertThat(response.getUserId()).isEqualTo(1L);
    }

    @Test
    void getProfile_throwsIfNotFound() {
        when(userProfileRepository.findByUserId(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userProfileService.getProfile(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Profile not found");
    }

    @Test
    void updateProfile_updatesFields() {
        UserProfile profile = UserProfile.builder()
                .id(1L)
                .userId(1L)
                .firstName("John")
                .lastName("Doe")
                .role(Role.STUDENT)
                .build();

        when(userProfileRepository.findByUserId(1L)).thenReturn(Optional.of(profile));
        when(userProfileRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setFirstName("Jane");
        request.setBio("Updated bio");

        UserProfileResponse response = userProfileService.updateProfile(1L, request);

        assertThat(response.getFirstName()).isEqualTo("Jane");
        assertThat(response.getBio()).isEqualTo("Updated bio");
        assertThat(response.getLastName()).isEqualTo("Doe");
    }
}