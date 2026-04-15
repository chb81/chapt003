package com.chapt003.util;

import com.chapt003.entity.User;
import com.chapt003.entity.enums.UserRole;
import com.chapt003.entity.enums.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtUtilTest {

    @InjectMocks
    private JwtUtil jwtUtil;

    private User testUser;

    @BeforeEach
    void setUp() throws Exception {
        Field secretField = JwtUtil.class.getDeclaredField("secret");
        secretField.setAccessible(true);
        secretField.set(jwtUtil, "mySecretKeyForJWTTokenGenerationAndValidation1234567890");

        Field expirationField = JwtUtil.class.getDeclaredField("expiration");
        expirationField.setAccessible(true);
        expirationField.set(jwtUtil, 86400000L);

        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setMobile("13800138000");
        testUser.setPassword("encodedPassword");
        testUser.setRole(UserRole.USER);
        testUser.setStatus(UserStatus.VERIFIED);
    }

    @Test
    void generateToken_WithValidUser_ShouldGenerateToken() {
        String token = jwtUtil.generateToken(testUser);

        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.split("\\.").length == 3);
    }

    @Test
    void generateToken_WithAdminRole_ShouldIncludeRole() {
        testUser.setRole(UserRole.ADMIN);
        String token = jwtUtil.generateToken(testUser);

        String role = jwtUtil.extractRole(token);
        assertEquals("ADMIN", role);
    }

    @Test
    void generateToken_WithUserRole_ShouldIncludeRole() {
        testUser.setRole(UserRole.USER);
        String token = jwtUtil.generateToken(testUser);

        String role = jwtUtil.extractRole(token);
        assertEquals("USER", role);
    }

    @Test
    void extractUsername_WithValidToken_ShouldReturnUsername() {
        String token = jwtUtil.generateToken(testUser);

        String username = jwtUtil.extractUsername(token);

        assertEquals("test@example.com", username);
    }

    @Test
    void extractUserId_WithValidToken_ShouldReturnUserId() {
        String token = jwtUtil.generateToken(testUser);

        Long userId = jwtUtil.extractUserId(token);

        assertEquals(1L, userId);
    }

    @Test
    void extractRole_WithValidToken_ShouldReturnRole() {
        String token = jwtUtil.generateToken(testUser);

        String role = jwtUtil.extractRole(token);

        assertEquals("USER", role);
    }

    @Test
    void extractExpiration_WithValidToken_ShouldReturnFutureDate() {
        String token = jwtUtil.generateToken(testUser);

        Date expiration = jwtUtil.extractExpiration(token);
        Date now = new Date();

        assertNotNull(expiration);
        assertTrue(expiration.after(now));
    }

    @Test
    void validateToken_WithValidTokenAndMatchingUsername_ShouldReturnTrue() {
        String token = jwtUtil.generateToken(testUser);

        Boolean isValid = jwtUtil.validateToken(token, "test@example.com");

        assertTrue(isValid);
    }

    @Test
    void validateToken_WithValidTokenButWrongUsername_ShouldReturnFalse() {
        String token = jwtUtil.generateToken(testUser);

        Boolean isValid = jwtUtil.validateToken(token, "wrong@example.com");

        assertFalse(isValid);
    }

    @Test
    void validateToken_WithExpiredToken_ShouldReturnFalse() throws Exception {
        Field expirationField = JwtUtil.class.getDeclaredField("expiration");
        expirationField.setAccessible(true);
        expirationField.set(jwtUtil, -1000L);

        String token = jwtUtil.generateToken(testUser);

        Boolean isValid = jwtUtil.validateToken(token, "test@example.com");

        assertFalse(isValid);
    }

    @Test
    void validateToken_WithNullToken_ShouldThrowException() {
        assertThrows(Exception.class, () -> {
            jwtUtil.validateToken(null, "test@example.com");
        });
    }

    @Test
    void validateToken_WithInvalidToken_ShouldThrowException() {
        String invalidToken = "invalid.token.here";

        assertThrows(Exception.class, () -> {
            jwtUtil.validateToken(invalidToken, "test@example.com");
        });
    }

    @Test
    void extractUsername_WithInvalidToken_ShouldThrowException() {
        String invalidToken = "invalid.token.here";

        assertThrows(Exception.class, () -> {
            jwtUtil.extractUsername(invalidToken);
        });
    }

    @Test
    void extractUserId_WithInvalidToken_ShouldThrowException() {
        String invalidToken = "invalid.token.here";

        assertThrows(Exception.class, () -> {
            jwtUtil.extractUserId(invalidToken);
        });
    }

    @Test
    void extractRole_WithInvalidToken_ShouldThrowException() {
        String invalidToken = "invalid.token.here";

        assertThrows(Exception.class, () -> {
            jwtUtil.extractRole(invalidToken);
        });
    }

    @Test
    void extractExpiration_WithInvalidToken_ShouldThrowException() {
        String invalidToken = "invalid.token.here";

        assertThrows(Exception.class, () -> {
            jwtUtil.extractExpiration(invalidToken);
        });
    }

    @Test
    void generateToken_WithUserWithNullRole_ShouldUseDefaultRole() {
        testUser.setRole(null);
        String token = jwtUtil.generateToken(testUser);

        String role = jwtUtil.extractRole(token);

        assertEquals("USER", role);
    }

    @Test
    void extractAllClaims_WithValidToken_ShouldReturnAllClaims() {
        String token = jwtUtil.generateToken(testUser);

        String username = jwtUtil.extractUsername(token);
        Long userId = jwtUtil.extractUserId(token);
        String role = jwtUtil.extractRole(token);
        Date expiration = jwtUtil.extractExpiration(token);

        assertEquals("test@example.com", username);
        assertEquals(1L, userId);
        assertEquals("USER", role);
        assertNotNull(expiration);
    }
}
