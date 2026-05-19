package com.skillbridge.user.controller;

import com.skillbridge.user.dto.MentorSearchResponse;
import com.skillbridge.user.service.MentorSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.util.List;

@RestController
@RequestMapping("/api/mentors")
@RequiredArgsConstructor
public class MentorSearchController {

    private final MentorSearchService mentorSearchService;

    @GetMapping("/search")
    public ResponseEntity<List<MentorSearchResponse>> searchBySkill(
            @RequestParam String skill) {
        return ResponseEntity.ok(mentorSearchService.searchBySkill(skill));
    }

    @GetMapping("/available")
    public ResponseEntity<List<MentorSearchResponse>> searchByDay(
            @RequestParam DayOfWeek day) {
        return ResponseEntity.ok(mentorSearchService.searchByDay(day));
    }
}