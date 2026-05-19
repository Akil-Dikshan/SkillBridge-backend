package com.skillbridge.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MentorSearchResponse {

    private Long userId;
    private String firstName;
    private String lastName;
    private String bio;
    private String profilePictureUrl;
    private String skills;
    private BigDecimal hourlyRate;
    private Integer yearsOfExperience;
    private BigDecimal averageRating;
    private List<String> availableDays;
}