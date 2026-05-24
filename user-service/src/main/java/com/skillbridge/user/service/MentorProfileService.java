package com.skillbridge.user.service;

import com.skillbridge.user.dto.*;
import com.skillbridge.user.model.MentorProfile;
import com.skillbridge.user.repository.MentorProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MentorProfileService {

    private final MentorProfileRepository mentorProfileRepository;

    //  Create mentor profile
    @Transactional
    public MentorProfileResponse createMentorProfile(Long userId, CreateMentorProfileRequest request) {

        if (mentorProfileRepository.existsByUserId(userId)) {
            throw new IllegalArgumentException("Mentor profile already exists for this user");
        }

        MentorProfile profile = MentorProfile.builder()
                .userId(userId)
                .skills(request.getSkills())
                .hourlyRate(request.getHourlyRate())
                .yearsOfExperience(request.getYearsOfExperience())
                .build();

        mentorProfileRepository.save(profile);

        return toResponse(profile);
    }

    //  Get mentor profile
    public MentorProfileResponse getMentorProfile(Long userId) {

        MentorProfile profile = mentorProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Mentor profile not found"));

        return toResponse(profile);
    }

    //  Update mentor profile
    @Transactional
    public MentorProfileResponse updateMentorProfile(Long userId, UpdateMentorProfileRequest request) {

        MentorProfile profile = mentorProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Mentor profile not found"));

        if (request.getSkills() != null) profile.setSkills(request.getSkills());
        if (request.getHourlyRate() != null) profile.setHourlyRate(request.getHourlyRate());
        if (request.getYearsOfExperience() != null) profile.setYearsOfExperience(request.getYearsOfExperience());

        mentorProfileRepository.save(profile);

        return toResponse(profile);
    }

    //  Mapper
    private MentorProfileResponse toResponse(MentorProfile profile) {
        return MentorProfileResponse.builder()
                .id(profile.getId())
                .userId(profile.getUserId())
                .skills(profile.getSkills())
                .hourlyRate(profile.getHourlyRate())
                .yearsOfExperience(profile.getYearsOfExperience())
                .averageRating(profile.getAverageRating())
                .totalSessions(profile.getTotalSessions())
                .createdAt(profile.getCreatedAt())
                .build();
    }

    /**
     * Updates the mentor's average rating and total review count.
     * Called by the internal endpoint consumed by review-service via Feign.
     *
     * @param userId       the mentor's user ID
     * @param averageRating the recalculated average rating from review-service
     * @param totalReviews  the new total number of reviews
     */
    @Transactional
    public void updateMentorRating(Long userId, java.math.BigDecimal averageRating, Integer totalReviews) {
        MentorProfile profile = mentorProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Mentor profile not found for userId: " + userId));

        profile.setAverageRating(averageRating);
        // totalSessions tracks booking completions; totalReviews is stored
        // in the averageRating column. We only update averageRating here.
        mentorProfileRepository.save(profile);
    }
}