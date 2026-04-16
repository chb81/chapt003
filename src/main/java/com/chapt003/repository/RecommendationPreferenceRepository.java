package com.chapt003.repository;

import com.chapt003.entity.RecommendationPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RecommendationPreferenceRepository extends JpaRepository<RecommendationPreference, Long> {

    Optional<RecommendationPreference> findByUserIdAndDeletedFalse(Long userId);

    @Query("SELECT r FROM RecommendationPreference r WHERE r.user.id = :userId AND r.deleted = false")
    Optional<RecommendationPreference> findActiveByUserId(@Param("userId") Long userId);
}
