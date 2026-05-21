package com.skillbridge.booking.client.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {

    private Long id;
    private Long userId;
    private String firstName;
    private String lastName;
    private String role;
}
