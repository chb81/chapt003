package com.chapt003.repository;

import com.chapt003.entity.TbSchool;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface TbSchoolRepository extends JpaRepository<TbSchool, Long> {

    Optional<TbSchool> findBySchoolCode(String schoolCode);

    boolean existsBySchoolCode(String schoolCode);

    boolean existsBySchoolName(String schoolName);

    Optional<TbSchool> findBySchoolName(String schoolName);

    List<TbSchool> findBySchoolType(String schoolType);

    List<TbSchool> findBySchoolTypeAndAreaCode(String schoolType, String areaCode);

    List<TbSchool> findBySchoolTypeAndSchoolRank(String schoolType, Integer schoolRank);

    List<TbSchool> findByAreaCode(String areaCode);

    List<TbSchool> findByCity(String city);

    List<TbSchool> findByCityAndDistrict(String city, String district);

    @Query("SELECT DISTINCT s.city FROM TbSchool s WHERE s.city IS NOT NULL ORDER BY s.city")
    List<String> findAllCities();

    @Query("SELECT DISTINCT s.district FROM TbSchool s WHERE s.city = :city AND s.district IS NOT NULL ORDER BY s.district")
    List<String> findDistrictsByCity(@Param("city") String city);

    @Query("SELECT s FROM TbSchool s WHERE " +
           "(:keyword IS NULL OR LOWER(s.schoolName) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(:city IS NULL OR s.city = :city) AND " +
           "(:district IS NULL OR s.district = :district) AND " +
           "(:schoolType IS NULL OR s.schoolType = :schoolType OR s.schoolNature = :schoolType) AND " +
           "(:minScore IS NULL OR s.admissionScoreYear1 >= :minScore OR s.admissionScoreYear2 >= :minScore OR s.admissionScoreYear3 >= :minScore) AND " +
           "(:maxScore IS NULL OR s.admissionScoreYear1 <= :maxScore OR s.admissionScoreYear2 <= :maxScore OR s.admissionScoreYear3 <= :maxScore) AND " +
           "(s.deleted = false OR s.deleted IS NULL)")
    Page<TbSchool> searchSchools(@Param("keyword") String keyword,
                                  @Param("city") String city,
                                  @Param("district") String district,
                                  @Param("schoolType") String schoolType,
                                  @Param("minScore") BigDecimal minScore,
                                  @Param("maxScore") BigDecimal maxScore,
                                  Pageable pageable);

    @Query("SELECT COUNT(s) FROM TbSchool s WHERE s.schoolType = :schoolType")
    long countBySchoolType(@Param("schoolType") String schoolType);

    @Query("SELECT s FROM TbSchool s WHERE s.schoolName LIKE %:name%")
    List<TbSchool> findBySchoolNameContaining(@Param("name") String name);
}
