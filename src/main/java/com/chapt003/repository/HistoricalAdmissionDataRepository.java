package com.chapt003.repository;

import com.chapt003.entity.HistoricalAdmissionData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoricalAdmissionDataRepository extends JpaRepository<HistoricalAdmissionData, Long> {

    List<HistoricalAdmissionData> findBySchoolIdOrderByYearDesc(Long schoolId);

    @Query("SELECT h FROM HistoricalAdmissionData h WHERE " +
            "(:year IS NULL OR h.year = :year) AND " +
            "(:city IS NULL OR h.city = :city) AND " +
            "(:district IS NULL OR h.district = :district) AND " +
            "(:schoolType IS NULL OR h.schoolType = :schoolType) AND " +
            "(:keyword IS NULL OR h.schoolName LIKE %:keyword%)")
    Page<HistoricalAdmissionData> findWithFilters(
            @Param("year") Integer year,
            @Param("city") String city,
            @Param("district") String district,
            @Param("schoolType") String schoolType,
            @Param("keyword") String keyword,
            Pageable pageable);

    boolean existsBySchoolIdAndYear(Long schoolId, Integer year);
}
