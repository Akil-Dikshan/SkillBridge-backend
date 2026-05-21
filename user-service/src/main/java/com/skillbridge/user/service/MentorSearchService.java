package com.skillbridge.user.service;

import com.skillbridge.user.dto.MentorSearchResponse;
import com.skillbridge.user.model.MentorAvailability;
import com.skillbridge.user.model.MentorProfile;
import com.skillbridge.user.model.UserProfile;
import com.skillbridge.user.repository.MentorAvailabilityRepository;
import com.skillbridge.user.repository.MentorProfileRepository;
import com.skillbridge.user.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MentorSearchService {

    private final MentorProfileRepository mentorProfileRepository;
    private final UserProfileRepository userProfileRepository;
    private final MentorAvailabilityRepository availabilityRepository;

    public List<MentorSearchResponse> searchBySkill(String skill) {
        return mentorProfileRepository.findBySkillsContainingIgnoreCase(skill)
                .stream()
                .map(this::toSearchResponse)
                .collect(Collectors.toList());
    }

    public List<MentorSearchResponse> searchByDay(DayOfWeek day) {
        return availabilityRepository.findByDayOfWeekAndActiveTrue(day)
                .stream()
                .map(slot -> mentorProfileRepository.findByUserId(slot.getUserId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(this::toSearchResponse)
                .collect(Collectors.toList());
    }

    private MentorSearchResponse toSearchResponse(MentorProfile mentor) {
        Optional<UserProfile> profile = userProfileRepository.findByUserId(mentor.getUserId());

        List<String> availableDays = availabilityRepository
                .findByUserId(mentor.getUserId())
                .stream()
                .filter(MentorAvailability::isActive)
                .map(slot -> slot.getDayOfWeek().name())
                .collect(Collectors.toList());

        return MentorSearchResponse.builder()
                .userId(mentor.getUserId())
                .firstName(profile.map(UserProfile::getFirstName).orElse(null))
                .lastName(profile.map(UserProfile::getLastName).orElse(null))
                .bio(profile.map(UserProfile::getBio).orElse(null))
                .profilePictureUrl(profile.map(UserProfile::getProfilePictureUrl).orElse(null))
                .skills(mentor.getSkills())
                .hourlyRate(mentor.getHourlyRate())
                .yearsOfExperience(mentor.getYearsOfExperience())
                .averageRating(mentor.getAverageRating())
                .availableDays(availableDays)
                .build();
    }
}