package com.chapt003.repository;

import com.chapt003.entity.TbEnrollmentPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TbEnrollmentPlanRepository extends JpaRepository<TbEnrollmentPlan, Long> {

    List<TbEnrollmentPlan> findByPlanYearAndCityCode(String planYear, String cityCode);

    Optional<TbEnrollmentPlan> findBySchoolCodeAndPlanYear(String schoolCode, String planYear);

    List<TbEnrollmentPlan> findBySchoolCode(String schoolCode);
}
