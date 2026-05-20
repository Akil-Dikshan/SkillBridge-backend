package com.skillbridge.booking.repository;

import com.skillbridge.booking.model.Session;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SessionRepository extends JpaRepository<Session, Long> {

    Optional<Session> findByBookingId(Long bookingId);
}
