package com.skillbridge.review;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Entry point for the SkillBridge Review Service.
 *
 * This service:
 *   - Accepts post-session reviews from STUDENT users
 *   - Verifies the referenced booking is COMPLETED via Feign → booking-service
 *   - Calculates and pushes updated mentor average ratings via Feign → user-service
 *   - Exposes review data for frontend display
 *
 * Port: 8085
 */
@SpringBootApplication
@EnableFeignClients
public class ReviewServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReviewServiceApplication.class, args);
    }
}
