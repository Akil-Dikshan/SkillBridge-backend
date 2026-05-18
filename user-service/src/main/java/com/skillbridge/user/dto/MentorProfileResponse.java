package com.skillbridge.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MentorProfileResponse {

    private Long id;
    private Long userId;
    private String skills;
    private BigDecimal hourlyRate;
    private Integer yearsOfExperience;
    private BigDecimal averageRating;
    private Integer totalSessions;
    private LocalDateTime createdAt;
}