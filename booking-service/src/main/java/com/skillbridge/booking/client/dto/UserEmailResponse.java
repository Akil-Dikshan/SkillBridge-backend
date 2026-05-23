package com.skillbridge.booking.client.dto;

import lombok.Data;

@Data
public class UserEmailResponse {
    private Long id;
    private String email;
    private String role;
}
