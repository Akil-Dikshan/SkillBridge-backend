package com.skillbridge.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EducationRequest {

    @NotBlank(message = "School is required")
    private String school;

    private String degree;
    private String fieldOfStudy;
    private Integer startYear;
    private Integer endYear;
    private String description;
}
