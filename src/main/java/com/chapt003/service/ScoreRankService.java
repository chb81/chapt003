package com.chapt003.service;

import com.chapt003.dto.ScoreRankResponse;
import com.chapt003.entity.ScoreRankMapping;
import com.chapt003.repository.ScoreRankMappingRepository;
import com.chapt003.repository.StudentProfileRepository;
import com.chapt003.repository.StudentScoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Year;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ScoreRankService {

    @Autowired
    private ScoreRankMappingRepository scoreRankMappingRepository;

    @Autowired
    private StudentScoreRepository studentScoreRepository;

    @Autowired
    private StudentProfileRepository studentProfileRepository;

    public ScoreRankResponse convertScoreToRank(BigDecimal totalScore, String city, Integer year) {
        if (year == null) {
            year = Year.now().getValue();
        }

        Optional<ScoreRankMapping> mappingOpt = scoreRankMappingRepository
                .findByCityAndYearAndTotalScore(city, year, totalScore);

        if (!mappingOpt.isPresent()) {
            List<ScoreRankMapping> aboveScores = scoreRankMappingRepository
                    .findRanksAboveScore(city, year, totalScore);
            if (aboveScores.isEmpty()) {
                return ScoreRankResponse.builder()
                        .totalScore(totalScore)
                        .city(null)
                        .year(year)
                        .message("未找到该分数对应的位次数据")
                        .build();
            }
            ScoreRankMapping nearest = aboveScores.get(0);
            return buildResponse(nearest, totalScore);
        }

        return buildResponse(mappingOpt.get(), totalScore);
    }

    public ScoreRankResponse getStudentRank(Long userId) {
        Optional<com.chapt003.entity.StudentScore> scoreOpt = studentScoreRepository.findByUserId(userId);
        if (!scoreOpt.isPresent() || scoreOpt.get().getTotalScore() == null) {
            return ScoreRankResponse.builder()
                    .message("请先录入学生成绩")
                    .build();
        }

        Optional<com.chapt003.entity.StudentProfile> profileOpt = studentProfileRepository.findByUserId(userId);
        if (!profileOpt.isPresent() || profileOpt.get().getCity() == null) {
            return ScoreRankResponse.builder()
                    .message("请先完善学生地区信息")
                    .build();
        }

        return convertScoreToRank(
                scoreOpt.get().getTotalScore(),
                profileOpt.get().getCity(),
                Year.now().getValue()
        );
    }

    public List<Integer> getAvailableYears(String city) {
        return scoreRankMappingRepository.findAvailableYears(city);
    }

    private ScoreRankResponse buildResponse(ScoreRankMapping mapping, BigDecimal totalScore) {
        Integer totalStudents = scoreRankMappingRepository.getTotalStudents(mapping.getCity(), mapping.getYear());
        BigDecimal percentile = null;
        if (totalStudents != null && totalStudents > 0 && mapping.getCumulativeCount() != null) {
            percentile = BigDecimal.ONE
                    .subtract(new BigDecimal(mapping.getCumulativeCount()).divide(new BigDecimal(totalStudents), 4, RoundingMode.HALF_UP))
                    .multiply(new BigDecimal("100"))
                    .setScale(2, RoundingMode.HALF_UP);
        }

        return ScoreRankResponse.builder()
                .totalScore(totalScore)
                .cityRank(mapping.getCityRank())
                .district(mapping.getDistrict())
                .districtRank(mapping.getDistrictRank())
                .totalStudents(totalStudents)
                .percentile(percentile)
                .year(mapping.getYear())
                .city(mapping.getCity())
                .build();
    }
}
