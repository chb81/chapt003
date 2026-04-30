package com.chapt003.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "allocation_quotas")
@SQLDelete(sql = "UPDATE allocation_quotas SET deleted = true WHERE id = ?")
@Where(clause = "deleted = false")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class AllocationQuota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "school_id", nullable = false)
    private Long schoolId;

    @Column(name = "source_school_name", nullable = false, length = 100)
    private String sourceSchoolName;

    @Column(name = "source_school_city", length = 50)
    private String sourceSchoolCity;

    @Column(name = "source_school_district", length = 50)
    private String sourceSchoolDistrict;

    @Column(nullable = false)
    private Integer year;

    @Column(name = "quota_count", nullable = false)
    @Builder.Default
    private Integer quotaCount = 0;

    @Column(name = "admission_score", precision = 5, scale = 2)
    private BigDecimal admissionScore;

    @Column(name = "unified_score", precision = 5, scale = 2)
    private BigDecimal unifiedScore;

    @Column(name = "score_difference", precision = 5, scale = 2)
    private BigDecimal scoreDifference;

    @Column(name = "policy_rule", length = 200)
    private String policyRule;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    @Builder.Default
    private Boolean deleted = false;

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
