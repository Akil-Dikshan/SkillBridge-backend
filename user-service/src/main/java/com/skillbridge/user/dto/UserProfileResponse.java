package com.skillbridge.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserProfileResponse {

    private Long id;
    private Long userId;
    private String firstName;
    private String lastName;
    private String bio;
    private String profilePictureUrl;
    private String role;
    private LocalDateTime createdAt;
}