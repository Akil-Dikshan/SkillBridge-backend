package com.skillbridge.user.service;

import com.skillbridge.user.dto.*;
import com.skillbridge.user.model.Education;
import com.skillbridge.user.model.WorkExperience;
import com.skillbridge.user.repository.EducationRepository;
import com.skillbridge.user.repository.WorkExperienceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResumeService {

    private final WorkExperienceRepository workRepo;
    private final EducationRepository educationRepo;

    // ── Work Experience ──

    public WorkExperienceResponse addWorkExperience(Long userId, WorkExperienceRequest request) {
        WorkExperience entity = WorkExperience.builder()
                .userId(userId)
                .jobTitle(request.getJobTitle())
                .company(request.getCompany())
                .startDate(request.getStartDate())
                .endDate(request.isCurrent() ? null : request.getEndDate())
                .isCurrent(request.isCurrent())
                .location(request.getLocation())
                .description(request.getDescription())
                .build();
        return toWorkResponse(workRepo.save(entity));
    }

    public List<WorkExperienceResponse> getWorkExperience(Long userId) {
        return workRepo.findByUserIdOrderByIdDesc(userId)
                .stream().map(this::toWorkResponse).collect(Collectors.toList());
    }

    public void deleteWorkExperience(Long userId, Long entryId) {
        WorkExperience entry = workRepo.findById(entryId)
                .orElseThrow(() -> new IllegalArgumentException("Work experience entry not found"));
        if (!entry.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Not authorised to delete this entry");
        }
        workRepo.delete(entry);
    }

    // ── Education ──

    public EducationResponse addEducation(Long userId, EducationRequest request) {
        Education entity = Education.builder()
                .userId(userId)
                .school(request.getSchool())
                .degree(request.getDegree())
                .fieldOfStudy(request.getFieldOfStudy())
                .startYear(request.getStartYear())
                .endYear(request.getEndYear())
                .description(request.getDescription())
                .build();
        return toEduResponse(educationRepo.save(entity));
    }

    public List<EducationResponse> getEducation(Long userId) {
        return educationRepo.findByUserIdOrderByStartYearDesc(userId)
                .stream().map(this::toEduResponse).collect(Collectors.toList());
    }

    public void deleteEducation(Long userId, Long entryId) {
        Education entry = educationRepo.findById(entryId)
                .orElseThrow(() -> new IllegalArgumentException("Education entry not found"));
        if (!entry.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Not authorised to delete this entry");
        }
        educationRepo.delete(entry);
    }

    // ── Mappers ──

    private WorkExperienceResponse toWorkResponse(WorkExperience e) {
        return WorkExperienceResponse.builder()
                .id(e.getId())
                .userId(e.getUserId())
                .jobTitle(e.getJobTitle())
                .company(e.getCompany())
                .startDate(e.getStartDate())
                .endDate(e.getEndDate())
                .isCurrent(e.isCurrent())
                .location(e.getLocation())
                .description(e.getDescription())
                .build();
    }

    private EducationResponse toEduResponse(Education e) {
        return EducationResponse.builder()
                .id(e.getId())
                .userId(e.getUserId())
                .school(e.getSchool())
                .degree(e.getDegree())
                .fieldOfStudy(e.getFieldOfStudy())
                .startYear(e.getStartYear())
                .endYear(e.getEndYear())
                .description(e.getDescription())
                .build();
    }
}
