package com.skillbridge.user.repository;

import com.skillbridge.user.model.MentorProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;
@Repository
public interface MentorProfileRepository extends JpaRepository<MentorProfile, Long> {
    List<MentorProfile> findBySkillsContainingIgnoreCase(String skill);
    Optional<MentorProfile> findByUserId(Long userId);
    boolean existsByUserId(Long userId);
}