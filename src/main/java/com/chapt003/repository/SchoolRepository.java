package com.chapt003.repository;

import com.chapt003.entity.School;
import com.chapt003.entity.enums.SchoolType;
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
public interface SchoolRepository extends JpaRepository<School, Long> {

    Optional<School> findByName(String name);

    boolean existsByName(String name);

    List<School> findByCity(String city);

    List<School> findByCityAndDistrict(String city, String district);

    List<School> findByType(SchoolType type);

    @Query("SELECT s FROM School s WHERE " +
           "(:keyword IS NULL OR LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(:city IS NULL OR s.city = :city) AND " +
           "(:district IS NULL OR s.district = :district) AND " +
           "(:type IS NULL OR s.type = :type) AND " +
           "(:minScore IS NULL OR s.admissionScoreYear1 >= :minScore OR s.admissionScoreYear2 >= :minScore OR s.admissionScoreYear3 >= :minScore) AND " +
           "(:maxScore IS NULL OR s.admissionScoreYear1 <= :maxScore OR s.admissionScoreYear2 <= :maxScore OR s.admissionScoreYear3 <= :maxScore)")
    Page<School> searchSchools(@Param("keyword") String keyword,
                               @Param("city") String city,
                               @Param("district") String district,
                               @Param("type") SchoolType type,
                               @Param("minScore") BigDecimal minScore,
                               @Param("maxScore") BigDecimal maxScore,
                               Pageable pageable);

    @Query("SELECT DISTINCT s.city FROM School s ORDER BY s.city")
    List<String> findAllCities();

    @Query("SELECT DISTINCT s.district FROM School s WHERE s.city = :city ORDER BY s.district")
    List<String> findDistrictsByCity(@Param("city") String city);

    @Query("SELECT COUNT(s) FROM School s WHERE s.type = :type")
    long countByType(@Param("type") SchoolType type);
}
