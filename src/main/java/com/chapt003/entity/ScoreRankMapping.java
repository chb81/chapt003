package com.chapt003.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "score_rank_mapping")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ScoreRankMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String city;

    @Column(nullable = false)
    private Integer year;

    @Column(name = "total_score", nullable = false, precision = 5, scale = 2)
    private BigDecimal totalScore;

    @Column(name = "city_rank", nullable = false)
    private Integer cityRank;

    @Column(length = 50)
    private String district;

    @Column(name = "district_rank")
    private Integer districtRank;

    @Column(name = "student_count")
    private Integer studentCount;

    @Column(name = "cumulative_count")
    private Integer cumulativeCount;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
