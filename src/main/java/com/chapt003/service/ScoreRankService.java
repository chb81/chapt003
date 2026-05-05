package com.chapt003.service;

import com.chapt003.dto.ScoreRankMappingRequest;
import com.chapt003.dto.ScoreRankMappingResponse;
import com.chapt003.dto.ScoreRankResponse;
import com.chapt003.entity.ScoreRankMapping;
import com.chapt003.repository.ScoreRankMappingRepository;
import com.chapt003.repository.StudentProfileRepository;
import com.chapt003.repository.StudentScoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public Page<ScoreRankMappingResponse> getScoreRankList(String city, Integer year, Pageable pageable) {
        Page<ScoreRankMapping> page;
        if (city != null && year != null) {
            page = scoreRankMappingRepository.findByCityAndYear(city, year, pageable);
        } else if (year != null) {
            page = scoreRankMappingRepository.findByYear(year, pageable);
        } else if (city != null) {
            page = scoreRankMappingRepository.findByCity(city, pageable);
        } else {
            page = scoreRankMappingRepository.findAll(pageable);
        }
        return page.map(this::convertToResponse);
    }

    @Transactional
    public ScoreRankMappingResponse createScoreRankMapping(ScoreRankMappingRequest request) {
        ScoreRankMapping mapping = ScoreRankMapping.builder()
                .city(request.getCity())
                .year(request.getYear())
                .totalScore(request.getTotalScore())
                .cityRank(request.getCityRank())
                .district(request.getDistrict())
                .districtRank(request.getDistrictRank())
                .studentCount(request.getStudentCount())
                .cumulativeCount(request.getCumulativeCount())
                .build();
        mapping = scoreRankMappingRepository.save(mapping);
        return convertToResponse(mapping);
    }

    @Transactional
    public ScoreRankMappingResponse updateScoreRankMapping(Long id, ScoreRankMappingRequest request) {
        ScoreRankMapping mapping = scoreRankMappingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("分数位次数据不存在: " + id));
        mapping.setCity(request.getCity());
        mapping.setYear(request.getYear());
        mapping.setTotalScore(request.getTotalScore());
        mapping.setCityRank(request.getCityRank());
        mapping.setDistrict(request.getDistrict());
        mapping.setDistrictRank(request.getDistrictRank());
        mapping.setStudentCount(request.getStudentCount());
        mapping.setCumulativeCount(request.getCumulativeCount());
        mapping = scoreRankMappingRepository.save(mapping);
        return convertToResponse(mapping);
    }

    @Transactional
    public void deleteScoreRankMapping(Long id) {
        ScoreRankMapping mapping = scoreRankMappingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("分数位次数据不存在: " + id));
        scoreRankMappingRepository.delete(mapping);
    }

    @Transactional
    public int batchImportScoreRankMappings(List<ScoreRankMappingRequest> requests) {
        int count = 0;
        for (ScoreRankMappingRequest req : requests) {
            ScoreRankMapping mapping = ScoreRankMapping.builder()
                    .city(req.getCity())
                    .year(req.getYear())
                    .totalScore(req.getTotalScore())
                    .cityRank(req.getCityRank())
                    .district(req.getDistrict())
                    .districtRank(req.getDistrictRank())
                    .studentCount(req.getStudentCount())
                    .cumulativeCount(req.getCumulativeCount())
                    .build();
            scoreRankMappingRepository.save(mapping);
            count++;
        }
        return count;
    }

    private ScoreRankMappingResponse convertToResponse(ScoreRankMapping m) {
        return ScoreRankMappingResponse.builder()
                .id(m.getId())
                .city(m.getCity())
                .year(m.getYear())
                .totalScore(m.getTotalScore())
                .cityRank(m.getCityRank())
                .district(m.getDistrict())
                .districtRank(m.getDistrictRank())
                .studentCount(m.getStudentCount())
                .cumulativeCount(m.getCumulativeCount())
                .build();
    }
}
