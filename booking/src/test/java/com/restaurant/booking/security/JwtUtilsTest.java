package com.restaurant.booking.security;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;

class JwtUtilsTest {

    private JwtUtils jwtUtils;

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils();
        // Set test values using reflection
        ReflectionTestUtils.setField(jwtUtils, "secretBase64", "VGhpcy1pcy1hLWRldmVsb3BtZW50LXNlY3JldC1rZXk=");
        ReflectionTestUtils.setField(jwtUtils, "expirationMs", 3600000L); // 1 hour
    }

    @Test
    void generateToken_ValidInput_ReturnsValidToken() {
        // Given
        String subject = "test@example.com";
        String role = "USER";

        // When
        String token = jwtUtils.generateToken(subject, role);

        // Then
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.contains(".")); // JWT format has dots
    }

    @Test
    void generateToken_WithRole_IncludesRoleClaim() {
        // Given
        String subject = "admin@example.com";
        String role = "ADMIN";

        // When
        String token = jwtUtils.generateToken(subject, role);

        // Then
        assertNotNull(token);
        Claims claims = jwtUtils.parseClaims(token);
        assertEquals("admin@example.com", claims.getSubject());
        assertEquals("ADMIN", claims.get("role"));
    }

    @Test
    void generateToken_WithDifferentRoles_IncludesCorrectRole() {
        // Given
        String[] roles = {"USER", "ADMIN", "MANAGER"};
        String subject = "test@example.com";

        for (String role : roles) {
            // When
            String token = jwtUtils.generateToken(subject, role);

            // Then
            assertNotNull(token);
            Claims claims = jwtUtils.parseClaims(token);
            assertEquals(role, claims.get("role"));
        }
    }

    @Test
    void generateToken_WithEmptySubject_HandlesCorrectly() {
        // Given
        String subject = "";
        String role = "USER";

        // When
        String token = jwtUtils.generateToken(subject, role);

        // Then
        assertNotNull(token);
        Claims claims = jwtUtils.parseClaims(token);
        assertNull(claims.getSubject()); // JWT subject is null for empty string
        assertEquals("USER", claims.get("role"));
    }

    @Test
    void generateToken_WithNullRole_HandlesCorrectly() {
        // Given
        String subject = "test@example.com";
        String role = null;

        // When
        String token = jwtUtils.generateToken(subject, role);

        // Then
        assertNotNull(token);
        Claims claims = jwtUtils.parseClaims(token);
        assertEquals("test@example.com", claims.getSubject());
        assertNull(claims.get("role"));
    }

    @Test
    void parseClaims_ValidToken_ReturnsClaims() {
        // Given
        String subject = "test@example.com";
        String role = "USER";
        String token = jwtUtils.generateToken(subject, role);

        // When
        Claims claims = jwtUtils.parseClaims(token);

        // Then
        assertNotNull(claims);
        assertEquals(subject, claims.getSubject());
        assertEquals(role, claims.get("role"));
        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());
    }

    @Test
    void parseClaims_ExpiredToken_ThrowsException() {
        // Given
        JwtUtils expiredJwtUtils = new JwtUtils();
        ReflectionTestUtils.setField(expiredJwtUtils, "secretBase64", "VGhpcy1pcy1hLWRldmVsb3BtZW50LXNlY3JldC1rZXk=");
        ReflectionTestUtils.setField(expiredJwtUtils, "expirationMs", -1000L); // Negative expiration (already expired)
        
        String token = expiredJwtUtils.generateToken("test@example.com", "USER");

        // When & Then
        assertThrows(ExpiredJwtException.class, () -> {
            jwtUtils.parseClaims(token);
        });
    }

    @Test
    void parseClaims_InvalidToken_ThrowsException() {
        // Given
        String invalidToken = "invalid.jwt.token";

        // When & Then
        assertThrows(MalformedJwtException.class, () -> {
            jwtUtils.parseClaims(invalidToken);
        });
    }

    @Test
    void parseClaims_MalformedToken_ThrowsException() {
        // Given
        String malformedToken = "not-a-jwt-token";

        // When & Then
        assertThrows(MalformedJwtException.class, () -> {
            jwtUtils.parseClaims(malformedToken);
        });
    }

    @Test
    void parseClaims_EmptyToken_ThrowsException() {
        // Given
        String emptyToken = "";

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            jwtUtils.parseClaims(emptyToken);
        });
    }

    @Test
    void parseClaims_NullToken_ThrowsException() {
        // Given
        String nullToken = null;

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            jwtUtils.parseClaims(nullToken);
        });
    }

    @Test
    void parseClaims_TokenWithWrongSignature_ThrowsException() {
        // Given - Use a different secret that's long enough
        JwtUtils otherJwtUtils = new JwtUtils();
        ReflectionTestUtils.setField(otherJwtUtils, "secretBase64", "VGhpcy1pcy1hLWRpZmZlcmVudC1zZWNyZXQta2V5LWZvci10ZXN0aW5nLXB1cnBvc2VzLW9ubHk=");
        ReflectionTestUtils.setField(otherJwtUtils, "expirationMs", 3600000L);
        
        String token = otherJwtUtils.generateToken("test@example.com", "USER");

        // When & Then
        assertThrows(SignatureException.class, () -> {
            jwtUtils.parseClaims(token);
        });
    }

    @Test
    void generateToken_TokenContainsRequiredFields() {
        // Given
        String subject = "test@example.com";
        String role = "USER";

        // When
        String token = jwtUtils.generateToken(subject, role);
        Claims claims = jwtUtils.parseClaims(token);

        // Then
        assertNotNull(claims.getSubject());
        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());
        assertTrue(claims.getExpiration().after(claims.getIssuedAt()));
    }

    @Test
    void generateToken_ExpirationTime_IsCorrect() {
        // Given
        String subject = "test@example.com";
        String role = "USER";

        // When
        String token = jwtUtils.generateToken(subject, role);
        Claims claims = jwtUtils.parseClaims(token);

        // Then
        Date issuedAt = claims.getIssuedAt();
        Date expiration = claims.getExpiration();
        
        assertNotNull(issuedAt);
        assertNotNull(expiration);
        assertTrue(expiration.getTime() > issuedAt.getTime());
        // Check that expiration is approximately 1 hour from issued time (with 5 minute tolerance)
        long timeDiff = expiration.getTime() - issuedAt.getTime();
        assertTrue(timeDiff >= 3300000); // At least 55 minutes
        assertTrue(timeDiff <= 3900000); // At most 65 minutes
    }

    @Test
    void generateToken_WithSpecialCharacters_HandlesCorrectly() {
        // Given
        String subject = "test+user@example-domain.com";
        String role = "USER_WITH_SPECIAL_CHARS";

        // When
        String token = jwtUtils.generateToken(subject, role);

        // Then
        assertNotNull(token);
        Claims claims = jwtUtils.parseClaims(token);
        assertEquals(subject, claims.getSubject());
        assertEquals(role, claims.get("role"));
    }
}
