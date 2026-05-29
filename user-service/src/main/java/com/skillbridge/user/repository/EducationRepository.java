package com.skillbridge.user.repository;

import com.skillbridge.user.model.Education;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EducationRepository extends JpaRepository<Education, Long> {
    List<Education> findByUserIdOrderByStartYearDesc(Long userId);
}
