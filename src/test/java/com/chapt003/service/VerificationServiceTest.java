package com.chapt003.service;

import com.chapt003.entity.VerificationCode;
import com.chapt003.entity.enums.VerificationCodeType;
import com.chapt003.repository.VerificationCodeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VerificationServiceTest {

    @Mock
    private VerificationCodeRepository verificationCodeRepository;

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private VerificationService verificationService;

    @BeforeEach
    void setUp() {
    }

    @Test
    void generateAndSendVerificationCode_ShouldCreateAndSendCode() {
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));
        doNothing().when(verificationCodeRepository).deleteByEmailAndExpiresAtBefore(any(String.class), any(LocalDateTime.class));
        when(verificationCodeRepository.save(any(VerificationCode.class))).thenAnswer(invocation -> {
            VerificationCode vc = invocation.getArgument(0);
            vc.setId(1L);
            if (vc.getCreatedAt() == null) {
                vc.setCreatedAt(LocalDateTime.now());
            }
            return vc;
        });

        String code = verificationService.generateAndSendVerificationCode("test@example.com");

        ArgumentCaptor<VerificationCode> vcCaptor = ArgumentCaptor.forClass(VerificationCode.class);
        verify(verificationCodeRepository).save(vcCaptor.capture());

        VerificationCode savedCode = vcCaptor.getValue();
        assertEquals("test@example.com", savedCode.getEmail());
        assertNotNull(savedCode.getCode());
        assertEquals(6, savedCode.getCode().length());
        assertFalse(savedCode.isUsed());
        assertNotNull(savedCode.getCreatedAt());
        assertNotNull(savedCode.getExpiresAt());

        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(messageCaptor.capture());

        SimpleMailMessage message = messageCaptor.getValue();
        assertEquals("test@example.com", message.getTo()[0]);
        assertEquals("验证码 - 中考志愿填报系统", message.getSubject());
        assertTrue(message.getText().contains("验证码"));
    }

    @Test
    void verifyCode_WithValidCode_ShouldReturnTrueAndMarkAsUsed() {
        VerificationCode vc = new VerificationCode(
            "test@example.com",
            "123456",
            VerificationCodeType.EMAIL,
            LocalDateTime.now().plusMinutes(5)
        );
        vc.setId(1L);
        vc.setCreatedAt(LocalDateTime.now().minusMinutes(5));
        vc.setUsed(false);

        when(verificationCodeRepository.findByEmailAndUsedFalseAndExpiresAtAfter(eq("test@example.com"), any(LocalDateTime.class)))
            .thenReturn(Optional.of(vc));
        when(verificationCodeRepository.save(any(VerificationCode.class))).thenReturn(vc);

        boolean result = verificationService.verifyCode("test@example.com", "123456");

        assertTrue(result);
        assertTrue(vc.isUsed());
        verify(verificationCodeRepository).save(vc);
    }

    @Test
    void verifyCode_WithInvalidCode_ShouldReturnFalse() {
        VerificationCode vc = new VerificationCode(
            "test@example.com",
            "654321",
            VerificationCodeType.EMAIL,
            LocalDateTime.now().plusMinutes(5)
        );
        vc.setId(1L);
        vc.setCreatedAt(LocalDateTime.now().minusMinutes(5));
        vc.setUsed(false);

        when(verificationCodeRepository.findByEmailAndUsedFalseAndExpiresAtAfter(eq("test@example.com"), any(LocalDateTime.class)))
            .thenReturn(Optional.of(vc));

        boolean result = verificationService.verifyCode("test@example.com", "000000");

        assertFalse(result);
        verify(verificationCodeRepository, never()).save(any(VerificationCode.class));
    }

    @Test
    void verifyCode_WithExpiredCode_ShouldReturnFalse() {
        VerificationCode vc = new VerificationCode(
            "test@example.com",
            "123456",
            VerificationCodeType.EMAIL,
            LocalDateTime.now().minusMinutes(5)
        );
        vc.setId(1L);
        vc.setCreatedAt(LocalDateTime.now().minusMinutes(15));
        vc.setUsed(false);

        when(verificationCodeRepository.findByEmailAndUsedFalseAndExpiresAtAfter(eq("test@example.com"), any(LocalDateTime.class)))
            .thenReturn(Optional.of(vc));

        boolean result = verificationService.verifyCode("test@example.com", "123456");

        assertFalse(result);
    }

    @Test
    void verifyCode_WithUsedCode_ShouldReturnFalse() {
        VerificationCode vc = new VerificationCode(
            "test@example.com",
            "123456",
            VerificationCodeType.EMAIL,
            LocalDateTime.now().plusMinutes(5)
        );
        vc.setId(1L);
        vc.setCreatedAt(LocalDateTime.now().minusMinutes(5));
        vc.setUsed(true);

        when(verificationCodeRepository.findByEmailAndUsedFalseAndExpiresAtAfter(eq("test@example.com"), any(LocalDateTime.class)))
            .thenReturn(Optional.empty());

        boolean result = verificationService.verifyCode("test@example.com", "123456");

        assertFalse(result);
    }
}
