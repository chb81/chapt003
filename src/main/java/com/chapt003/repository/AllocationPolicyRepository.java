package com.chapt003.repository;

import com.chapt003.entity.AllocationPolicy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AllocationPolicyRepository extends JpaRepository<AllocationPolicy, Long> {

    List<AllocationPolicy> findByCityAndDistrictAndYear(String city, String district, Integer year);

    Optional<AllocationPolicy> findByCityAndDistrictAndYearAndPolicyTypeAndIsActiveTrue(String city, String district, Integer year, String policyType);

    @Query("SELECT ap FROM AllocationPolicy ap WHERE ap.isActive = true AND ap.year = :year")
    List<AllocationPolicy> findActiveByYear(@Param("year") Integer year);

    @Query("SELECT ap FROM AllocationPolicy ap WHERE ap.isActive = true AND ap.city = :city AND ap.year = :year")
    List<AllocationPolicy> findActiveByCityAndYear(@Param("city") String city, @Param("year") Integer year);

    Page<AllocationPolicy> findByCityAndDistrictAndYear(String city, String district, Integer year, Pageable pageable);

    Page<AllocationPolicy> findByYear(Integer year, Pageable pageable);

    Page<AllocationPolicy> findByCity(String city, Pageable pageable);
}
