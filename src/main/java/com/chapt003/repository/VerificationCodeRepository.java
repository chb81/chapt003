package com.chapt003.repository;

import com.chapt003.entity.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {

    Optional<VerificationCode> findByEmailAndUsedFalseAndExpiresAtAfter(
        String email, LocalDateTime now);

    @Modifying
    @Transactional
    void deleteByEmailAndExpiresAtBefore(String email, LocalDateTime now);

    @Query("SELECT vc FROM VerificationCode vc WHERE vc.email = :email AND vc.code = :code AND vc.used = false AND vc.expiresAt > :now")
    Optional<VerificationCode> findUnusedValidCode(@Param("email") String email, @Param("code") String code, @Param("now") LocalDateTime now);

    @Modifying
    @Transactional
    @Query("DELETE FROM VerificationCode vc WHERE vc.expiresAt < :now")
    void deleteExpiredCodes(@Param("now") LocalDateTime now);
}
