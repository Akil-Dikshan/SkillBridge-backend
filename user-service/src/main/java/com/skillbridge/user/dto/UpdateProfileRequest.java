package com.skillbridge.user.dto;

import lombok.Data;

@Data
public class UpdateProfileRequest {

    private String firstName;
    private String lastName;
    private String bio;
    private String profilePictureUrl;
}