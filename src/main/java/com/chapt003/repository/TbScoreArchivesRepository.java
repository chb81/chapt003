package com.chapt003.repository;

import com.chapt003.entity.TbScoreArchives;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TbScoreArchivesRepository extends JpaRepository<TbScoreArchives, Long> {

    Optional<TbScoreArchives> findByScoreAndExayYearAndCityCode(Integer score, String exayYear, String cityCode);

    List<TbScoreArchives> findByExayYearAndCityCodeOrderByScoreDesc(String exayYear, String cityCode);

    @Query("SELECT s FROM TbScoreArchives s WHERE s.exayYear = :year AND s.cityCode = :cityCode AND s.score >= :minScore ORDER BY s.score DESC")
    List<TbScoreArchives> findByYearAndCityAndScoreAbove(@Param("year") String year, @Param("cityCode") String cityCode, @Param("minScore") Integer minScore);

    @Query("SELECT DISTINCT s.exayYear FROM TbScoreArchives s WHERE s.cityCode = :cityCode ORDER BY s.exayYear DESC")
    List<String> findAvailableYears(@Param("cityCode") String cityCode);

    @Query("SELECT MAX(s.studentsTotalNum) FROM TbScoreArchives s WHERE s.exayYear = :year AND s.cityCode = :cityCode")
    Integer getTotalStudentsByYearAndCity(@Param("year") String year, @Param("cityCode") String cityCode);
}
