package com.skillbridge.booking.client.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MentorProfileResponse {

    private Long id;
    private Long userId;
    private String skills;
    private BigDecimal hourlyRate;
    private Integer yearsOfExperience;
}
