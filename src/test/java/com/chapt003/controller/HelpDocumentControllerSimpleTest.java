package com.chapt003.controller;

import com.chapt003.entity.HelpDocument;
import com.chapt003.entity.enums.HelpDocumentCategory;
import com.chapt003.repository.HelpDocumentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class HelpDocumentControllerSimpleTest {

    @Autowired
    private HelpDocumentRepository helpDocumentRepository;

    @Test
    void contextLoads() {
        // 测试基本配置
        assertNotNull(helpDocumentRepository);
        
        // 测试查询
        /* 
        var documents = helpDocumentRepository.findByCategoryAndPublishedAndDeletedFalse(
                HelpDocumentCategory.QUICK_START, true);
        System.out.println("Found documents: " + documents.size());
        */
    }
}
