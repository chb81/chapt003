package com.chapt003.repository;

import com.chapt003.entity.VolunteerApplication;
import com.chapt003.entity.enums.VolunteerApplicationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VolunteerApplicationRepository extends JpaRepository<VolunteerApplication, Long> {

    List<VolunteerApplication> findByUserId(Long userId);

    Optional<VolunteerApplication> findByIdAndUserId(Long id, Long userId);

    List<VolunteerApplication> findByUserIdAndYear(Long userId, Integer year);

    Optional<VolunteerApplication> findByUserIdAndYearAndStatus(Long userId, Integer year, VolunteerApplicationStatus status);

    @Query("SELECT va FROM VolunteerApplication va WHERE va.user.id = :userId AND va.status = :status")
    List<VolunteerApplication> findByUserIdAndStatus(@Param("userId") Long userId, @Param("status") VolunteerApplicationStatus status);

    @Query("SELECT va FROM VolunteerApplication va WHERE va.user.id = :userId AND va.status = :status")
    Page<VolunteerApplication> findByUserIdAndStatus(@Param("userId") Long userId, @Param("status") VolunteerApplicationStatus status, Pageable pageable);

    @Query("SELECT va FROM VolunteerApplication va WHERE va.user.id = :userId AND va.status = 'SIMULATION' ORDER BY va.createdAt DESC")
    Page<VolunteerApplication> findSimulationsByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT COUNT(va) FROM VolunteerApplication va WHERE va.user.id = :userId AND va.year = :year AND va.status = 'DRAFT'")
    long countDraftByUserIdAndYear(@Param("userId") Long userId, @Param("year") Integer year);

    boolean existsByUserIdAndYearAndStatus(Long userId, Integer year, VolunteerApplicationStatus status);

    long countByUserId(Long userId);

    @Query("SELECT va FROM VolunteerApplication va LEFT JOIN FETCH va.items WHERE va.id = :id")
    Optional<VolunteerApplication> findByIdWithItems(@Param("id") Long id);
}
