package com.chapt003.service;

import com.chapt003.entity.User;
import com.chapt003.entity.enums.UserStatus;
import com.chapt003.entity.enums.VerificationCodeType;
import com.chapt003.exception.BusinessException;
import com.chapt003.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class PasswordResetService {
    
    private static final Logger logger = LoggerFactory.getLogger(PasswordResetService.class);
    
    private static final int VERIFICATION_CODE_EXPIRY_MINUTES = 10;
    private static final int MAX_SEND_ATTEMPTS_PER_HOUR = 5;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private VerificationCodeService verificationCodeService;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private SmsService smsService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public void sendVerificationCode(String email, String mobile) {
        User user = null;
        String target = null;
        VerificationCodeType type = null;
        
        Optional<User> userOpt;
        if (email != null && !email.isEmpty()) {
            userOpt = userRepository.findByEmail(email);
            target = email;
            type = VerificationCodeType.EMAIL_PASSWORD_RESET;
        } else if (mobile != null && !mobile.isEmpty()) {
            userOpt = userRepository.findByMobile(mobile);
            target = mobile;
            type = VerificationCodeType.SMS_PASSWORD_RESET;
        } else {
            throw new BusinessException("请提供邮箱或手机号");
        }

        if (!userOpt.isPresent()) {
            throw new BusinessException(404, "用户不存在");
        }
        user = userOpt.get();
        
        if (user.getStatus() == UserStatus.DISABLED) {
            throw new BusinessException("账户已被禁用，请联系管理员");
        }
        
        verificationCodeService.checkSendRateLimit(target, type, MAX_SEND_ATTEMPTS_PER_HOUR);
        
        String code = verificationCodeService.generateAndStoreCode(target, type, VERIFICATION_CODE_EXPIRY_MINUTES);
        
        if (type == VerificationCodeType.EMAIL_PASSWORD_RESET) {
            emailService.sendPasswordResetCode(email, code);
        } else {
            smsService.sendPasswordResetCode(mobile, code);
        }
        
        logger.info("Password reset code sent to: {}", target);
    }
    
    @Transactional
    public void resetPassword(String email, String mobile, String code, String newPassword, String confirmPassword) {
        if (!newPassword.equals(confirmPassword)) {
            throw new BusinessException("两次输入的密码不一致");
        }
        
        if (!isPasswordStrong(newPassword)) {
            throw new BusinessException("密码至少8位，包含字母和数字");
        }
        
        User user = null;
        String target = null;
        VerificationCodeType type = null;
        
        if (email != null && !email.isEmpty()) {
            user = userRepository.findByEmail(email).orElse(null);
            target = email;
            type = VerificationCodeType.EMAIL_PASSWORD_RESET;
        } else if (mobile != null && !mobile.isEmpty()) {
            user = userRepository.findByMobile(mobile).orElse(null);
            target = mobile;
            type = VerificationCodeType.SMS_PASSWORD_RESET;
        } else {
            throw new BusinessException("请提供邮箱或手机号");
        }
        
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        
        if (!verificationCodeService.verifyCode(target, type, code)) {
            throw new BusinessException("验证码无效或已过期");
        }
        
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        
        logger.info("Password reset successfully for user: {}", user.getEmail());
    }
    
    private boolean isPasswordStrong(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        boolean hasLetter = false;
        boolean hasDigit = false;
        for (char c : password.toCharArray()) {
            if (Character.isLetter(c)) hasLetter = true;
            if (Character.isDigit(c)) hasDigit = true;
        }
        return hasLetter && hasDigit;
    }
}
