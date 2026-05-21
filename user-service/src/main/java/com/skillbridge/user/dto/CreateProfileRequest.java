package com.skillbridge.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateProfileRequest {

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    private String bio;

    private String profilePictureUrl;
}