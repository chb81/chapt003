package com.chapt003.aspect;

import com.chapt003.annotation.RateLimit;
import com.chapt003.exception.RateLimitException;
import javax.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.concurrent.TimeUnit;

@Aspect
@Component
public class RateLimitAspect {
    
    private static final Logger logger = LoggerFactory.getLogger(RateLimitAspect.class);
    
    private final StringRedisTemplate redisTemplate;
    
    @Autowired
    public RateLimitAspect(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
    
    @Around("@annotation(rateLimit)")
    public Object around(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String ip = getClientIp(request);
        String uri = request.getRequestURI();
        String key = "rate_limit:" + uri + ":" + ip;
        
        Long currentCount = redisTemplate.opsForValue().increment(key);
        
        if (currentCount != null && currentCount == 1) {
            redisTemplate.expire(key, rateLimit.timeWindow(), TimeUnit.MILLISECONDS);
        }
        
        if (currentCount != null && currentCount > rateLimit.maxRequests()) {
            long minutes = rateLimit.timeWindow() / 60000;
            logger.warn("Rate limit exceeded for IP: {}, URI: {}, Count: {}", ip, uri, currentCount);
            throw new RateLimitException("请求过于频繁，请" + minutes + "分钟后再试");
        }
        
        return joinPoint.proceed();
    }
    
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
