package com.skillbridge.user.repository;

import com.skillbridge.user.model.MentorAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.DayOfWeek;
import java.util.List;

@Repository
public interface MentorAvailabilityRepository extends JpaRepository<MentorAvailability, Long> {
    List<MentorAvailability> findByUserId(Long userId);
    List<MentorAvailability> findByUserIdAndDayOfWeek(Long userId, DayOfWeek dayOfWeek);
    List<MentorAvailability> findByDayOfWeekAndActiveTrue(DayOfWeek dayOfWeek);
}