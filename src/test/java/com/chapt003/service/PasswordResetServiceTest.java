package com.chapt003.service;

import com.chapt003.entity.User;
import com.chapt003.entity.enums.UserStatus;
import com.chapt003.exception.InvalidVerificationCodeException;
import com.chapt003.exception.RateLimitExceededException;
import com.chapt003.exception.UserNotFoundException;
import com.chapt003.repository.UserRepository;
import com.chapt003.entity.enums.VerificationCodeType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasswordResetServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private VerificationCodeService verificationCodeService;

    @Mock
    private EmailService emailService;

    @Mock
    private SmsService smsService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private PasswordResetService passwordResetService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setMobile("13800138000");
        testUser.setPassword("oldEncodedPassword");
        testUser.setStatus(UserStatus.VERIFIED);
    }

    @Test
    void sendVerificationCode_WithEmail_ShouldSendEmail() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(verificationCodeService.generateAndStoreCode(anyString(), eq(VerificationCodeType.EMAIL_PASSWORD_RESET), anyInt()))
            .thenReturn("123456");

        passwordResetService.sendVerificationCode("test@example.com", null);

        verify(emailService).sendPasswordResetCode("test@example.com", "123456");
        verify(smsService, never()).sendPasswordResetCode(anyString(), anyString());
    }

    @Test
    void sendVerificationCode_WithMobile_ShouldSendSms() {
        when(userRepository.findByMobile("13800138000")).thenReturn(Optional.of(testUser));
        when(verificationCodeService.generateAndStoreCode(anyString(), eq(VerificationCodeType.SMS_PASSWORD_RESET), anyInt()))
            .thenReturn("654321");

        passwordResetService.sendVerificationCode(null, "13800138000");

        verify(smsService).sendPasswordResetCode("13800138000", "654321");
        verify(emailService, never()).sendPasswordResetCode(anyString(), anyString());
    }

    @Test
    void sendVerificationCode_WithNonExistentEmail_ShouldThrowException() {
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        assertThrows(
            UserNotFoundException.class,
            () -> passwordResetService.sendVerificationCode("nonexistent@example.com", null)
        );

        verify(emailService, never()).sendPasswordResetCode(anyString(), anyString());
    }

    @Test
    void sendVerificationCode_WithNonExistentMobile_ShouldThrowException() {
        when(userRepository.findByMobile("13900139000")).thenReturn(Optional.empty());

        assertThrows(
            UserNotFoundException.class,
            () -> passwordResetService.sendVerificationCode(null, "13900139000")
        );

        verify(smsService, never()).sendPasswordResetCode(anyString(), anyString());
    }

    @Test
    void sendVerificationCode_WithRateLimit_ShouldThrowException() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        doThrow(new RateLimitExceededException("发送频率过高，请稍后再试"))
            .when(verificationCodeService).checkSendRateLimit(anyString(), eq(VerificationCodeType.EMAIL_PASSWORD_RESET), anyInt());

        assertThrows(
            RateLimitExceededException.class,
            () -> passwordResetService.sendVerificationCode("test@example.com", null)
        );

        verify(emailService, never()).sendPasswordResetCode(anyString(), anyString());
    }

    @Test
    void resetPassword_WithEmail_ShouldSucceed() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(verificationCodeService.verifyCode(anyString(), eq(VerificationCodeType.EMAIL_PASSWORD_RESET), eq("123456")))
            .thenReturn(true);
        when(passwordEncoder.encode("NewPassword123")).thenReturn("newEncodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        passwordResetService.resetPassword("test@example.com", null, "123456", "NewPassword123", "NewPassword123");

        assertEquals("newEncodedPassword", testUser.getPassword());
        verify(userRepository).save(testUser);
        verify(verificationCodeService).verifyCode("test@example.com", VerificationCodeType.EMAIL_PASSWORD_RESET, "123456");
    }

    @Test
    void resetPassword_WithMobile_ShouldSucceed() {
        when(userRepository.findByMobile("13800138000")).thenReturn(Optional.of(testUser));
        when(verificationCodeService.verifyCode(anyString(), eq(VerificationCodeType.SMS_PASSWORD_RESET), eq("654321")))
            .thenReturn(true);
        when(passwordEncoder.encode("NewPassword123")).thenReturn("newEncodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        passwordResetService.resetPassword(null, "13800138000", "654321", "NewPassword123", "NewPassword123");

        assertEquals("newEncodedPassword", testUser.getPassword());
        verify(userRepository).save(testUser);
        verify(verificationCodeService).verifyCode("13800138000", VerificationCodeType.SMS_PASSWORD_RESET, "654321");
    }

    @Test
    void resetPassword_WithNonExistentUser_ShouldThrowException() {
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        assertThrows(
            UserNotFoundException.class,
            () -> passwordResetService.resetPassword("nonexistent@example.com", null, "123456", "NewPassword123", "NewPassword123")
        );

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void resetPassword_WithInvalidCode_ShouldThrowException() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(verificationCodeService.verifyCode(anyString(), eq(VerificationCodeType.EMAIL_PASSWORD_RESET), eq("000000")))
            .thenReturn(false);

        assertThrows(
            InvalidVerificationCodeException.class,
            () -> passwordResetService.resetPassword("test@example.com", null, "000000", "NewPassword123", "NewPassword123")
        );

        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void resetPassword_WithPasswordMismatch_ShouldThrowException() {
        assertThrows(
            IllegalArgumentException.class,
            () -> passwordResetService.resetPassword("test@example.com", null, "123456", "NewPassword123", "DifferentPassword123")
        );

        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }
}