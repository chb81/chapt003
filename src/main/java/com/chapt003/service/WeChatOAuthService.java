package com.chapt003.service;

import com.chapt003.dto.WeChatLoginRequest;
import com.chapt003.dto.WeChatLoginResponse;
import com.chapt003.dto.WeChatSessionResponse;
import com.chapt003.entity.User;
import com.chapt003.entity.enums.UserRole;
import com.chapt003.entity.enums.UserStatus;
import com.chapt003.exception.AuthenticationException;
import com.chapt003.repository.UserRepository;
import com.chapt003.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.dao.DataIntegrityViolationException;

@Service
public class WeChatOAuthService {
    
    private static final Logger logger = LoggerFactory.getLogger(WeChatOAuthService.class);
    
    private static final String WECHAT_CODE2SESSION_URL = 
        "https://api.weixin.qq.com/sns/jscode2session?appid={appid}&secret={secret}&js_code={code}&grant_type=authorization_code";
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Value("${wechat.appid}")
    private String appId;
    
    @Value("${wechat.secret}")
    private String appSecret;
    
    @Transactional
    public WeChatLoginResponse loginWithWeChat(WeChatLoginRequest request) {
        logger.info("WeChat login attempt with code: {}", request.getCode());
        
        WeChatSessionResponse session = exchangeCodeForSession(request.getCode());
        
        if (session.getErrcode() != null && session.getErrcode() != 0) {
            logger.error("WeChat code2session failed: errcode={}, errmsg={}", 
                session.getErrcode(), session.getErrmsg());
            throw new AuthenticationException("微信授权失败: " + session.getErrmsg());
        }
        
        String openid = session.getOpenid();
        if (openid == null || openid.isEmpty()) {
            logger.error("WeChat openid is null or empty");
            throw new AuthenticationException("获取微信用户标识失败");
        }

        Optional<User> userOpt = userRepository.findByWechatOpenId(openid);
        boolean isNewUser = !userOpt.isPresent();
        User user = userOpt.orElse(null);
        
        if (isNewUser) {
            try {
                user = createWeChatUser(openid);
                logger.info("Created new WeChat user with openid: {}", openid);
            } catch (DataIntegrityViolationException e) {
                logger.warn("Concurrent user creation detected for openid: {}, retrying", openid);
                user = userRepository.findByWechatOpenId(openid).orElse(null);
                if (user == null) {
                    logger.error("Failed to find user after integrity violation for openid: {}", openid);
                    throw new AuthenticationException("创建用户失败，请重试");
                }
                isNewUser = false;
                logger.info("Found existing WeChat user after retry for openid: {}", openid);
            }
        } else {
            logger.info("Found existing WeChat user with openid: {}", openid);
        }
        
        String token = jwtUtil.generateToken(user);
        
        logger.info("WeChat login successful for user: {}", user.getId());
        
        return WeChatLoginResponse.builder()
            .token(token)
            .userId(user.getId())
            .email(user.getEmail())
            .mobile(user.getMobile())
            .role(user.getRole().name())
            .expiresIn(jwtUtil.extractExpiration(token).getTime() - System.currentTimeMillis())
            .isNewUser(isNewUser)
            .build();
    }
    
    private WeChatSessionResponse exchangeCodeForSession(String code) {
        Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put("appid", appId);
        uriVariables.put("secret", appSecret);
        uriVariables.put("code", code);
        
        try {
            ResponseEntity<WeChatSessionResponse> response = restTemplate.getForEntity(
                WECHAT_CODE2SESSION_URL, 
                WeChatSessionResponse.class, 
                uriVariables
            );
            
            return response.getBody();
        } catch (Exception e) {
            logger.error("Error calling WeChat code2session API", e);
            throw new AuthenticationException("微信服务暂时不可用，请稍后重试");
        }
    }
    
    private User createWeChatUser(String openid) {
        User user = new User();
        
        String emailPrefix = openid.length() >= 8 ? openid.substring(0, 8) : openid;
        String mobileSuffix = openid.length() >= 2 ? openid.substring(0, 2) : openid.substring(0, 1);
        
        user.setEmail("wechat_" + emailPrefix + "@wechat.local");
        user.setMobile("10000000" + mobileSuffix);
        user.setPassword(UUID.randomUUID().toString());
        user.setWechatOpenId(openid);
        user.setStatus(UserStatus.ACTIVE);
        user.setRole(UserRole.USER);
        
        return userRepository.save(user);
    }
}
