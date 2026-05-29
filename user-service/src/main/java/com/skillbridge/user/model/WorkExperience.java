package com.skillbridge.user.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "work_experience")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class WorkExperience {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "job_title", nullable = false, length = 200)
    private String jobTitle;

    @Column(nullable = false, length = 200)
    private String company;

    @Column(name = "start_date", length = 20)
    private String startDate;

    @Column(name = "end_date", length = 20)
    private String endDate;

    @JsonProperty("isCurrent")
    @Column(name = "is_current")
    private boolean isCurrent = false;

    @Column(length = 200)
    private String location;

    @Column(columnDefinition = "TEXT")
    private String description;
}
