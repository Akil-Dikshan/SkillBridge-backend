package com.skillbridge.user.repository;

import com.skillbridge.user.model.WorkExperience;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkExperienceRepository extends JpaRepository<WorkExperience, Long> {
    List<WorkExperience> findByUserIdOrderByIdDesc(Long userId);
}
