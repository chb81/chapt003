package com.chapt003.controller;

import com.chapt003.entity.Announcement;
import com.chapt003.entity.enums.AnnouncementType;
import com.chapt003.repository.AnnouncementRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class AnnouncementControllerSimpleTest {

    @Autowired
    private AnnouncementRepository announcementRepository;

    @MockBean
    private StringRedisTemplate redisTemplate;

    @MockBean
    private ValueOperations<String, String> valueOperations;

    @Test
    void contextLoads() {
        // 测试基本配置
        assertNotNull(announcementRepository);
        
        // 测试查询
        java.util.List<Announcement> announcements = announcementRepository.findByTypeAndActive(
                AnnouncementType.IMPORTANT, java.time.LocalDateTime.now());
        System.out.println("Found announcements: " + announcements.size());
    }
}