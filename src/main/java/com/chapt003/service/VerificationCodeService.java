package com.chapt003.service;

import com.chapt003.entity.enums.VerificationCodeType;
import com.chapt003.exception.RateLimitException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class VerificationCodeService {
    
    private static final Logger logger = LoggerFactory.getLogger(VerificationCodeService.class);
    
    @Autowired
    private StringRedisTemplate redisTemplate;
    
    private static final int CODE_LENGTH = 6;
    private static final int MAX_SEND_ATTEMPTS_DEFAULT = 5;
    private static final Duration RATE_LIMIT_WINDOW = Duration.ofHours(1);
    
    public String generateAndStoreCode(String target, VerificationCodeType type, int expiryMinutes) {
        String code = generateRandomCode();
        String key = buildCodeKey(target, type);
        
        redisTemplate.opsForValue().set(
            key,
            code,
            expiryMinutes,
            TimeUnit.MINUTES
        );
        
        logger.info("Generated verification code for target: {}, type: {}", target, type);
        return code;
    }
    
    public boolean verifyCode(String target, VerificationCodeType type, String code) {
        String key = buildCodeKey(target, type);
        String storedCode = redisTemplate.opsForValue().get(key);
        
        if (storedCode == null) {
            logger.warn("Verification code not found or expired for target: {}, type: {}", target, type);
            return false;
        }
        
        if (!storedCode.equals(code)) {
            logger.warn("Invalid verification code for target: {}, type: {}", target, type);
            return false;
        }
        
        invalidateCode(target, type);
        return true;
    }
    
    public void checkSendRateLimit(String target, VerificationCodeType type, int maxAttempts) {
        String rateLimitKey = buildRateLimitKey(target, type);
        String attempts = redisTemplate.opsForValue().get(rateLimitKey);
        
        if (attempts != null && Integer.parseInt(attempts) >= maxAttempts) {
            logger.warn("Rate limit exceeded for target: {}, type: {}", target, type);
            throw new RateLimitException("发送频率过高，请稍后重试");
        }
        
        if (attempts == null) {
            redisTemplate.opsForValue().set(
                rateLimitKey,
                "1",
                RATE_LIMIT_WINDOW.getSeconds(),
                TimeUnit.SECONDS
            );
        } else {
            redisTemplate.opsForValue().increment(rateLimitKey);
        }
    }
    
    public void invalidateCode(String target, VerificationCodeType type) {
        String key = buildCodeKey(target, type);
        redisTemplate.delete(key);
        logger.info("Invalidated verification code for target: {}, type: {}", target, type);
    }
    
    private String buildCodeKey(String target, VerificationCodeType type) {
        return "verification:code:" + type.name().toLowerCase() + ":" + target;
    }
    
    private String buildRateLimitKey(String target, VerificationCodeType type) {
        return "verification:ratelimit:" + type.name().toLowerCase() + ":" + target;
    }
    
    private String generateRandomCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }
}
