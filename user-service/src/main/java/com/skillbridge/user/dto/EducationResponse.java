package com.skillbridge.user.dto;

import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class EducationResponse {
    private Long id;
    private Long userId;
    private String school;
    private String degree;
    private String fieldOfStudy;
    private Integer startYear;
    private Integer endYear;
    private String description;
}
