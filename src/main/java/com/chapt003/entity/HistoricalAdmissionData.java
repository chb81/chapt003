package com.chapt003.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "historical_admission_data")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistoricalAdmissionData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "school_id", nullable = false)
    private Long schoolId;

    @Column(name = "school_name", nullable = false, length = 100)
    private String schoolName;

    @Column(nullable = false)
    private Integer year;

    @Column(name = "admission_score", precision = 5, scale = 2)
    private BigDecimal admissionScore;

    @Column(name = "lowest_score", precision = 5, scale = 2)
    private BigDecimal lowestScore;

    @Column(name = "highest_score", precision = 5, scale = 2)
    private BigDecimal highestScore;

    @Column(name = "average_score", precision = 5, scale = 2)
    private BigDecimal averageScore;

    @Column(name = "enrollment_quota")
    private Integer enrollmentQuota;

    @Column(name = "applicant_count")
    private Integer applicantCount;

    @Column(name = "admission_rate", precision = 5, scale = 2)
    private BigDecimal admissionRate;

    @Column(length = 50)
    private String city;

    @Column(length = 50)
    private String district;

    @Column(name = "school_type", length = 30)
    private String schoolType;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
