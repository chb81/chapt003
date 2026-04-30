package com.chapt003.repository;

import com.chapt003.entity.AllocationQuota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AllocationQuotaRepository extends JpaRepository<AllocationQuota, Long> {

    List<AllocationQuota> findBySchoolIdAndYear(Long schoolId, Integer year);

    List<AllocationQuota> findBySourceSchoolNameAndYear(String sourceSchoolName, Integer year);

    @Query("SELECT aq FROM AllocationQuota aq WHERE aq.schoolId = :schoolId AND aq.sourceSchoolName = :sourceName AND aq.year = :year")
    Optional<AllocationQuota> findBySchoolAndSourceAndYear(@Param("schoolId") Long schoolId, @Param("sourceName") String sourceSchoolName, @Param("year") Integer year);

    @Query("SELECT DISTINCT aq.sourceSchoolName FROM AllocationQuota aq WHERE aq.schoolId = :schoolId AND aq.year = :year")
    List<String> findSourceSchoolsByTargetSchoolAndYear(@Param("schoolId") Long schoolId, @Param("year") Integer year);

    @Query("SELECT aq FROM AllocationQuota aq WHERE aq.sourceSchoolCity = :city AND aq.sourceSchoolDistrict = :district AND aq.year = :year")
    List<AllocationQuota> findByCityAndDistrictAndYear(@Param("city") String city, @Param("district") String district, @Param("year") Integer year);

    @Query("SELECT SUM(aq.quotaCount) FROM AllocationQuota aq WHERE aq.schoolId = :schoolId AND aq.year = :year")
    Integer getTotalQuotaBySchoolAndYear(@Param("schoolId") Long schoolId, @Param("year") Integer year);
}
