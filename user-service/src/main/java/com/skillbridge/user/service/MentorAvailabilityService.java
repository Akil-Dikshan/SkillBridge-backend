package com.skillbridge.user.service;

import com.skillbridge.user.dto.AvailabilityRequest;
import com.skillbridge.user.dto.AvailabilityResponse;
import com.skillbridge.user.model.MentorAvailability;
import com.skillbridge.user.repository.MentorAvailabilityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MentorAvailabilityService {

    private final MentorAvailabilityRepository availabilityRepository;

    @Transactional
    public AvailabilityResponse addAvailability(Long userId, AvailabilityRequest request) {

        MentorAvailability slot = MentorAvailability.builder()
                .userId(userId)
                .dayOfWeek(request.getDayOfWeek())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .active(true)
                .build();

        availabilityRepository.save(slot);

        return toResponse(slot);
    }

    public List<AvailabilityResponse> getAvailability(Long userId) {
        return availabilityRepository.findByUserId(userId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteAvailability(Long userId, Long slotId) {
        MentorAvailability slot = availabilityRepository.findById(slotId)
                .orElseThrow(() -> new IllegalArgumentException("Availability slot not found"));

        if (!slot.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Slot does not belong to this user");
        }

        availabilityRepository.delete(slot);
    }

    private AvailabilityResponse toResponse(MentorAvailability slot) {
        return AvailabilityResponse.builder()
                .id(slot.getId())
                .userId(slot.getUserId())
                .dayOfWeek(slot.getDayOfWeek())
                .startTime(slot.getStartTime())
                .endTime(slot.getEndTime())
                .active(slot.isActive())
                .build();
    }
}