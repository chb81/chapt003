package com.chapt003.service;

import com.chapt003.entity.VerificationCode;
import com.chapt003.entity.enums.VerificationCodeType;
import com.chapt003.repository.VerificationCodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class VerificationService {
    
    @Autowired
    private VerificationCodeRepository verificationCodeRepository;
    
    @Autowired
    private JavaMailSender mailSender;
    
    private static final int CODE_EXPIRATION_MINUTES = 10;
    private static final int CODE_LENGTH = 6;
    
    @Transactional
    public String generateAndSendVerificationCode(String email) {
        String code = generateRandomCode();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(CODE_EXPIRATION_MINUTES);
        
        verificationCodeRepository.deleteByEmailAndExpiresAtBefore(email, LocalDateTime.now());

        VerificationCode verificationCode = new VerificationCode(email, code, VerificationCodeType.EMAIL, expiresAt);
        verificationCodeRepository.save(verificationCode);
        
        sendVerificationEmail(email, code);
        
        return code;
    }
    
    @Transactional
    public boolean verifyCode(String email, String code) {
        Optional<VerificationCode> verificationCodeOpt = verificationCodeRepository
            .findByEmailAndUsedFalseAndExpiresAtAfter(email, LocalDateTime.now());
        
        if (!verificationCodeOpt.isPresent()) {
            return false;
        }
        
        VerificationCode verificationCode = verificationCodeOpt.get();
        
        if (!verificationCode.getCode().equals(code)) {
            return false;
        }
        
        if (verificationCode.isExpired()) {
            return false;
        }
        
        verificationCode.setUsed(true);
        verificationCodeRepository.save(verificationCode);
        
        return true;
    }
    
    private String generateRandomCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }
    
    private void sendVerificationEmail(String email, String code) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("验证码 - 中考志愿填报系统");
            message.setText("您的验证码是: " + code + "\n\n验证码有效期为10分钟，请勿泄露给他人。");
            message.setFrom("noreply@chapt003.com");
            
            mailSender.send(message);
        } catch (Exception e) {
            System.out.println("[DEV] 邮件发送失败，验证码: " + code + " (收件人: " + email + ")");
        }
    }
}
