package com.skillbridge.auth.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {

    private JwtService jwtService;

    private static final String SECRET =
            "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
    private static final long EXPIRATION = 86400000L;
    private static final long REFRESH_EXPIRATION = 604800000L;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey", SECRET);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", EXPIRATION);
        ReflectionTestUtils.setField(jwtService, "refreshExpiration", REFRESH_EXPIRATION);
    }

    @Test
    void generateAccessToken_returnsNonNullToken() {
        String token = jwtService.generateAccessToken("test@skillbridge.com", "STUDENT");
        assertThat(token).isNotNull();
        assertThat(token.split("\\.")).hasSize(3);
    }

    @Test
    void extractEmail_returnsCorrectEmail() {
        String email = "test@skillbridge.com";
        String token = jwtService.generateAccessToken(email, "STUDENT");
        assertThat(jwtService.extractEmail(token)).isEqualTo(email);
    }

    @Test
    void extractRole_returnsCorrectRole() {
        String token = jwtService.generateAccessToken("test@skillbridge.com", "MENTOR");
        assertThat(jwtService.extractRole(token)).isEqualTo("MENTOR");
    }

    @Test
    void isTokenValid_returnsTrueForValidToken() {
        String email = "test@skillbridge.com";
        String token = jwtService.generateAccessToken(email, "STUDENT");
        assertThat(jwtService.isTokenValid(token, email)).isTrue();
    }

    @Test
    void isTokenValid_returnsFalseForWrongEmail() {
        String token = jwtService.generateAccessToken("test@skillbridge.com", "STUDENT");
        assertThat(jwtService.isTokenValid(token, "other@skillbridge.com")).isFalse();
    }

    @Test
    void generateRefreshToken_hasNoRoleClaim() {
        String token = jwtService.generateRefreshToken("test@skillbridge.com");
        assertThat(jwtService.extractRole(token)).isNull();
    }
}