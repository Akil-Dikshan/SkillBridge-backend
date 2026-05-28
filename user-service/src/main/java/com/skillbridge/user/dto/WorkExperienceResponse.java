package com.skillbridge.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class WorkExperienceResponse {
    private Long id;
    private Long userId;
    private String jobTitle;
    private String company;
    private String startDate;
    private String endDate;

    @JsonProperty("isCurrent")
    private boolean isCurrent;

    private String location;
    private String description;
}
