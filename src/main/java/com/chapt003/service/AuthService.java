package com.chapt003.service;

import com.chapt003.annotation.RateLimit;
import com.chapt003.dto.LoginRequest;
import com.chapt003.dto.LoginResponse;
import com.chapt003.dto.RegisterRequest;
import com.chapt003.dto.RegisterResponse;
import com.chapt003.entity.User;
import com.chapt003.entity.enums.UserStatus;
import com.chapt003.exception.AuthenticationException;
import com.chapt003.exception.DuplicateUserException;
import com.chapt003.exception.UnverifiedAccountException;
import com.chapt003.repository.UserRepository;
import com.chapt003.util.JwtUtil;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.micrometer.core.instrument.Counter;

@Service
public class AuthService {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private VerificationService verificationService;
    
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private Counter userLoginCounter;

    @Autowired
    private Counter userRegistrationCounter;
    
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);
    
    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("两次输入的密码不一致");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateUserException("该邮箱已被注册");
        }
        
        if (userRepository.existsByMobile(request.getMobile())) {
            throw new DuplicateUserException("该手机号已被注册");
        }
        
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User user = new User();
        user.setUsername(request.getEmail());
        user.setEmail(request.getEmail());
        user.setMobile(request.getMobile());
        user.setPassword(encodedPassword);
        user.setStatus(UserStatus.UNVERIFIED);

        User savedUser = userRepository.save(user);
        
        verificationService.generateAndSendVerificationCode(request.getEmail());

        userRegistrationCounter.increment();

        RegisterResponse response = new RegisterResponse();
        response.setUserId(savedUser.getId());
        response.setEmail(savedUser.getEmail());
        response.setMobile(savedUser.getMobile());
        response.setMessage("注册成功，请查收验证邮件");
        return response;
    }
    
    @Transactional
    public boolean verifyEmail(String email, String code) {
        boolean verified = verificationService.verifyCode(email, code);
        
        if (verified) {
            User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
            
            user.setStatus(UserStatus.VERIFIED);
            userRepository.save(user);
        }
        
        return verified;
    }
    
    public boolean validatePassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
    
    @Transactional
    public void resendVerificationCode(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        
        if (user.getStatus() == UserStatus.VERIFIED) {
            throw new IllegalArgumentException("用户已验证，无需重新发送验证码");
        }
        
        verificationService.generateAndSendVerificationCode(email);
    }
    
    @RateLimit(maxRequests = 5, timeWindow = 60000)
    public LoginResponse login(LoginRequest request, HttpServletRequest httpRequest) {
        String emailOrMobile = request.getEmailOrMobile();
        String password = request.getPassword();
        User user = userRepository.findByEmailOrMobile(emailOrMobile)
            .orElseThrow(() -> {
                logger.warn("Login attempt with non-existent user: {}", emailOrMobile);
                return new AuthenticationException("邮箱或手机号或密码错误");
            });
        
        if (!passwordEncoder.matches(password, user.getPassword())) {
            logger.warn("Login attempt with wrong password for user: {}", emailOrMobile);
            throw new AuthenticationException("邮箱或手机号或密码错误");
        }
        
        if (user.getStatus() == UserStatus.DISABLED) {
            logger.warn("Login attempt by disabled user: {}", emailOrMobile);
            throw new AuthenticationException("您的账户已被禁用，请联系管理员");
        }
        
        if (user.getStatus() == UserStatus.DELETED) {
            logger.warn("Login attempt by deleted user: {}", emailOrMobile);
            throw new AuthenticationException("邮箱或手机号或密码错误");
        }

        if (user.getStatus() == UserStatus.UNVERIFIED) {
            logger.warn("Login attempt by unverified user (allowed in dev): {}", emailOrMobile);
        }
        
        String token = jwtUtil.generateToken(user);
        
        logger.info("User logged in successfully: {}", emailOrMobile);
        
        userLoginCounter.increment();
        
        return LoginResponse.builder()
            .token(token)
            .userId(user.getId())
            .email(user.getEmail())
            .mobile(user.getMobile())
            .role(user.getRole().name())
            .expiresIn(jwtUtil.extractExpiration(token).getTime() - System.currentTimeMillis())
            .build();
    }
}
