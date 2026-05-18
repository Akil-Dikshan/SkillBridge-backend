package com.skillbridge.user.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class UpdateMentorProfileRequest {

    private String skills;
    private BigDecimal hourlyRate;
    private Integer yearsOfExperience;
}