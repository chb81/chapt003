package com.chapt003.service;

import com.chapt003.dto.LoginRequest;
import com.chapt003.dto.LoginResponse;
import com.chapt003.dto.RegisterRequest;
import com.chapt003.dto.RegisterResponse;
import com.chapt003.entity.User;
import com.chapt003.entity.enums.UserRole;
import com.chapt003.entity.enums.UserStatus;
import com.chapt003.exception.AuthenticationException;
import com.chapt003.exception.DuplicateUserException;
import com.chapt003.exception.UnverifiedAccountException;
import com.chapt003.repository.UserRepository;
import com.chapt003.util.JwtUtil;
import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private VerificationService verificationService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private HttpServletRequest httpRequest;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest validRequest;

    private LoginRequest validLoginRequest;

    private User testUser;

    @BeforeEach
    void setUp() {
        validRequest = new RegisterRequest();
        validRequest.setEmail("test@example.com");
        validRequest.setMobile("13800138000");
        validRequest.setPassword("Password123");
        validRequest.setConfirmPassword("Password123");

        validLoginRequest = new LoginRequest();
        validLoginRequest.setEmailOrMobile("test@example.com");
        validLoginRequest.setPassword("Password123");

        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setMobile("13800138000");
        testUser.setPassword("encodedPassword");
        testUser.setRole(UserRole.USER);
        testUser.setStatus(UserStatus.VERIFIED);
    }

    @Test
    void register_WithValidData_ShouldSucceed() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByMobile(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return user;
        });

        RegisterResponse response = authService.register(validRequest);

        assertNotNull(response);
        assertEquals(1L, response.getUserId());
        assertEquals("test@example.com", response.getEmail());
        assertEquals("13800138000", response.getMobile());
        assertEquals("注册成功，请查收验证邮件", response.getMessage());
        
        verify(userRepository).save(any(User.class));
        verify(verificationService).generateAndSendVerificationCode("test@example.com");
    }

    @Test
    void register_WithPasswordMismatch_ShouldThrowException() {
        validRequest.setConfirmPassword("Different123");

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> authService.register(validRequest)
        );

        assertEquals("两次输入的密码不一致", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void register_WithDuplicateEmail_ShouldThrowException() {
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        assertThrows(
            DuplicateUserException.class,
            () -> authService.register(validRequest)
        );

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void register_WithDuplicateMobile_ShouldThrowException() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByMobile("13800138000")).thenReturn(true);

        assertThrows(
            DuplicateUserException.class,
            () -> authService.register(validRequest)
        );

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void verifyEmail_WithValidCode_ShouldUpdateStatus() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setStatus(UserStatus.UNVERIFIED);

        when(verificationService.verifyCode("test@example.com", "123456")).thenReturn(true);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        boolean result = authService.verifyEmail("test@example.com", "123456");

        assertTrue(result);
        assertEquals(UserStatus.VERIFIED, user.getStatus());
        verify(userRepository).save(user);
    }

    @Test
    void verifyEmail_WithInvalidCode_ShouldReturnFalse() {
        when(verificationService.verifyCode("test@example.com", "000000")).thenReturn(false);

        boolean result = authService.verifyEmail("test@example.com", "000000");

        assertFalse(result);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void validatePassword_WithCorrectPassword_ShouldReturnTrue() {
        when(passwordEncoder.matches("Password123", "encodedPassword")).thenReturn(true);

        boolean result = authService.validatePassword("Password123", "encodedPassword");

        assertTrue(result);
    }

    @Test
    void validatePassword_WithIncorrectPassword_ShouldReturnFalse() {
        when(passwordEncoder.matches("WrongPassword", "encodedPassword")).thenReturn(false);

        boolean result = authService.validatePassword("WrongPassword", "encodedPassword");

        assertFalse(result);
    }

    @Test
    void resendVerificationCode_WithUnverifiedUser_ShouldSucceed() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setStatus(UserStatus.UNVERIFIED);

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        authService.resendVerificationCode("test@example.com");

        verify(verificationService).generateAndSendVerificationCode("test@example.com");
    }

    @Test
    void resendVerificationCode_WithVerifiedUser_ShouldThrowException() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setStatus(UserStatus.VERIFIED);

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> authService.resendVerificationCode("test@example.com")
        );

        assertEquals("用户已验证，无需重新发送验证码", exception.getMessage());
        verify(verificationService, never()).generateAndSendVerificationCode(anyString());
    }

    @Test
    void login_WithValidCredentials_ShouldSucceed() {
        when(userRepository.findByEmailOrMobile("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("Password123", "encodedPassword")).thenReturn(true);
        when(jwtUtil.generateToken(testUser)).thenReturn("valid.jwt.token");
        when(jwtUtil.extractExpiration("valid.jwt.token")).thenReturn(new Date(System.currentTimeMillis() + 86400000L));

        LoginResponse response = authService.login(validLoginRequest, httpRequest);

        assertNotNull(response);
        assertEquals("valid.jwt.token", response.getToken());
        assertEquals(1L, response.getUserId());
        assertEquals("test@example.com", response.getEmail());
        assertEquals("13800138000", response.getMobile());
        assertEquals("USER", response.getRole());
        assertNotNull(response.getExpiresIn());
        assertTrue(response.getExpiresIn() > 0);

        verify(userRepository).findByEmailOrMobile("test@example.com");
        verify(passwordEncoder).matches("Password123", "encodedPassword");
        verify(jwtUtil).generateToken(testUser);
    }

    @Test
    void login_WithMobileNumber_ShouldSucceed() {
        validLoginRequest.setEmailOrMobile("13800138000");
        when(userRepository.findByEmailOrMobile("13800138000")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("Password123", "encodedPassword")).thenReturn(true);
        when(jwtUtil.generateToken(testUser)).thenReturn("valid.jwt.token");
        when(jwtUtil.extractExpiration("valid.jwt.token")).thenReturn(new Date(System.currentTimeMillis() + 86400000L));

        LoginResponse response = authService.login(validLoginRequest, httpRequest);

        assertNotNull(response);
        assertEquals("valid.jwt.token", response.getToken());
        verify(userRepository).findByEmailOrMobile("13800138000");
    }

    @Test
    void login_WithNonExistentUser_ShouldThrowException() {
        when(userRepository.findByEmailOrMobile("nonexistent@example.com")).thenReturn(Optional.empty());

        LoginRequest request = new LoginRequest();
        request.setEmailOrMobile("nonexistent@example.com");
        request.setPassword("Password123");

        assertThrows(
            AuthenticationException.class,
            () -> authService.login(request, httpRequest)
        );

        verify(userRepository).findByEmailOrMobile("nonexistent@example.com");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtUtil, never()).generateToken(any(User.class));
    }

    @Test
    void login_WithWrongPassword_ShouldThrowException() {
        when(userRepository.findByEmailOrMobile("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("WrongPassword", "encodedPassword")).thenReturn(false);

        LoginRequest request = new LoginRequest();
        request.setEmailOrMobile("test@example.com");
        request.setPassword("WrongPassword");

        assertThrows(
            AuthenticationException.class,
            () -> authService.login(request, httpRequest)
        );

        verify(userRepository).findByEmailOrMobile("test@example.com");
        verify(passwordEncoder).matches("WrongPassword", "encodedPassword");
        verify(jwtUtil, never()).generateToken(any(User.class));
    }

    @Test
    void login_WithUnverifiedUser_ShouldThrowException() {
        testUser.setStatus(UserStatus.UNVERIFIED);
        when(userRepository.findByEmailOrMobile("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("Password123", "encodedPassword")).thenReturn(true);

        assertThrows(
            UnverifiedAccountException.class,
            () -> authService.login(validLoginRequest, httpRequest)
        );

        verify(userRepository).findByEmailOrMobile("test@example.com");
        verify(passwordEncoder).matches("Password123", "encodedPassword");
        verify(jwtUtil, never()).generateToken(any(User.class));
    }

    @Test
    void login_WithVerifiedUser_ShouldSucceed() {
        testUser.setStatus(UserStatus.VERIFIED);
        when(userRepository.findByEmailOrMobile("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("Password123", "encodedPassword")).thenReturn(true);
        when(jwtUtil.generateToken(testUser)).thenReturn("valid.jwt.token");
        when(jwtUtil.extractExpiration("valid.jwt.token")).thenReturn(new Date(System.currentTimeMillis() + 86400000L));

        LoginResponse response = authService.login(validLoginRequest, httpRequest);

        assertNotNull(response);
        assertEquals("valid.jwt.token", response.getToken());
        verify(jwtUtil).generateToken(testUser);
    }

    @Test
    void login_WithActiveUser_ShouldSucceed() {
        testUser.setStatus(UserStatus.ACTIVE);
        when(userRepository.findByEmailOrMobile("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("Password123", "encodedPassword")).thenReturn(true);
        when(jwtUtil.generateToken(testUser)).thenReturn("valid.jwt.token");
        when(jwtUtil.extractExpiration("valid.jwt.token")).thenReturn(new Date(System.currentTimeMillis() + 86400000L));

        LoginResponse response = authService.login(validLoginRequest, httpRequest);

        assertNotNull(response);
        assertEquals("valid.jwt.token", response.getToken());
        verify(jwtUtil).generateToken(testUser);
    }

    @Test
    void login_WithAdminUser_ShouldReturnAdminRole() {
        testUser.setRole(UserRole.ADMIN);
        when(userRepository.findByEmailOrMobile("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("Password123", "encodedPassword")).thenReturn(true);
        when(jwtUtil.generateToken(testUser)).thenReturn("admin.jwt.token");
        when(jwtUtil.extractExpiration("admin.jwt.token")).thenReturn(new Date(System.currentTimeMillis() + 86400000L));

        LoginResponse response = authService.login(validLoginRequest, httpRequest);

        assertNotNull(response);
        assertEquals("ADMIN", response.getRole());
    }
}
