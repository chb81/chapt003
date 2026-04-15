package com.chapt003.repository;

import com.chapt003.entity.StudentScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentScoreRepository extends JpaRepository<StudentScore, Long> {
    
    Optional<StudentScore> findByUserId(Long userId);
    
    boolean existsByUserId(Long userId);
    
    void deleteByUserId(Long userId);
}
