package com.chapt003.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "student_scores")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentScore {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal chinese;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal math;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal english;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal physics;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal chemistry;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal politics;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal history;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal geography;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal biology;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal totalScore;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal averageScore;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        calculateTotals();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        calculateTotals();
    }

    private void calculateTotals() {
        this.totalScore = chinese.add(math).add(english).add(physics).add(chemistry)
                .add(politics).add(history).add(geography).add(biology);
        this.averageScore = totalScore.divide(BigDecimal.valueOf(9), 2, BigDecimal.ROUND_HALF_UP);
    }
}
