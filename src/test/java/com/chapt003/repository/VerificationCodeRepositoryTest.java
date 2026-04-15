package com.chapt003.repository;

import com.chapt003.entity.VerificationCode;
import com.chapt003.entity.enums.VerificationCodeType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class VerificationCodeRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private VerificationCodeRepository verificationCodeRepository;

    @Test
    void findUnusedValidCode_WithValidCode_ShouldReturnCode() {
        VerificationCode vc = new VerificationCode(
            "test@example.com",
            "123456",
            VerificationCodeType.EMAIL,
            LocalDateTime.now().plusMinutes(5)
        );
        vc.setCreatedAt(LocalDateTime.now().minusMinutes(5));
        vc.setUsed(false);
        entityManager.persistAndFlush(vc);

        Optional<VerificationCode> found = verificationCodeRepository.findUnusedValidCode(
            "test@example.com", "123456", LocalDateTime.now()
        );

        assertTrue(found.isPresent());
        assertEquals("test@example.com", found.get().getEmail());
        assertEquals("123456", found.get().getCode());
        assertFalse(found.get().isUsed());
    }

    @Test
    void findUnusedValidCode_WithUsedCode_ShouldReturnEmpty() {
        VerificationCode vc = new VerificationCode(
            "test@example.com",
            "123456",
            VerificationCodeType.EMAIL,
            LocalDateTime.now().plusMinutes(5)
        );
        vc.setCreatedAt(LocalDateTime.now().minusMinutes(5));
        vc.setUsed(true);
        entityManager.persistAndFlush(vc);

        Optional<VerificationCode> found = verificationCodeRepository.findUnusedValidCode(
            "test@example.com", "123456", LocalDateTime.now()
        );

        assertFalse(found.isPresent());
    }

    @Test
    void findUnusedValidCode_WithExpiredCode_ShouldReturnEmpty() {
        VerificationCode vc = new VerificationCode(
            "test@example.com",
            "123456",
            VerificationCodeType.EMAIL,
            LocalDateTime.now().minusMinutes(5)
        );
        vc.setCreatedAt(LocalDateTime.now().minusMinutes(15));
        vc.setUsed(false);
        entityManager.persistAndFlush(vc);

        Optional<VerificationCode> found = verificationCodeRepository.findUnusedValidCode(
            "test@example.com", "123456", LocalDateTime.now()
        );

        assertFalse(found.isPresent());
    }

    @Test
    void deleteExpiredCodes_ShouldDeleteExpiredCodes() {
        VerificationCode expiredVc = new VerificationCode(
            "test@example.com",
            "123456",
            VerificationCodeType.EMAIL,
            LocalDateTime.now().minusMinutes(5)
        );
        expiredVc.setCreatedAt(LocalDateTime.now().minusMinutes(15));
        expiredVc.setUsed(false);
        entityManager.persistAndFlush(expiredVc);

        VerificationCode validVc = new VerificationCode(
            "test@example.com",
            "789012",
            VerificationCodeType.EMAIL,
            LocalDateTime.now().plusMinutes(5)
        );
        validVc.setCreatedAt(LocalDateTime.now().minusMinutes(5));
        validVc.setUsed(false);
        entityManager.persistAndFlush(validVc);

        verificationCodeRepository.deleteExpiredCodes(LocalDateTime.now());

        Optional<VerificationCode> expiredFound = verificationCodeRepository.findUnusedValidCode(
            "test@example.com", "123456", LocalDateTime.now()
        );
        assertFalse(expiredFound.isPresent());

        Optional<VerificationCode> validFound = verificationCodeRepository.findUnusedValidCode(
            "test@example.com", "789012", LocalDateTime.now()
        );
        assertTrue(validFound.isPresent());
    }
}
