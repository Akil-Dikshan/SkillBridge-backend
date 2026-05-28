package com.skillbridge.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class WorkExperienceRequest {

    @NotBlank(message = "Job title is required")
    private String jobTitle;

    @NotBlank(message = "Company is required")
    private String company;

    private String startDate;
    private String endDate;

    @JsonProperty("isCurrent")
    private boolean isCurrent;

    private String location;
    private String description;
}
