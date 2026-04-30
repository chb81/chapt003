package com.chapt003.repository;

import com.chapt003.entity.ScoreRankMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScoreRankMappingRepository extends JpaRepository<ScoreRankMapping, Long> {

    Optional<ScoreRankMapping> findByCityAndYearAndTotalScore(String city, Integer year, java.math.BigDecimal totalScore);

    @Query("SELECT srm FROM ScoreRankMapping srm WHERE srm.city = :city AND srm.year = :year AND srm.totalScore >= :score ORDER BY srm.totalScore ASC")
    List<ScoreRankMapping> findRanksAboveScore(@Param("city") String city, @Param("year") Integer year, @Param("score") java.math.BigDecimal score);

    @Query("SELECT srm.cityRank FROM ScoreRankMapping srm WHERE srm.city = :city AND srm.year = :year AND srm.totalScore <= :score ORDER BY srm.totalScore DESC")
    List<Integer> findRankByScore(@Param("city") String city, @Param("year") Integer year, @Param("score") java.math.BigDecimal score);

    @Query("SELECT MAX(srm.cumulativeCount) FROM ScoreRankMapping srm WHERE srm.city = :city AND srm.year = :year")
    Integer getTotalStudents(@Param("city") String city, @Param("year") Integer year);

    @Query("SELECT DISTINCT srm.year FROM ScoreRankMapping srm WHERE srm.city = :city ORDER BY srm.year DESC")
    List<Integer> findAvailableYears(@Param("city") String city);
}
