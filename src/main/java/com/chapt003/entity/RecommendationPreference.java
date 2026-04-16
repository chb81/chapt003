package com.chapt003.entity;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "recommendation_preferences")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "preferred_districts", length = 500)
    private String preferredDistricts;

    @Column(name = "preferred_school_types", length = 200)
    private String preferredSchoolTypes;

    @Column(name = "preferred_school_levels", length = 200)
    private String preferredSchoolLevels;

    @Column(name = "min_probability")
    @Builder.Default
    private Integer minProbability = 30;

    @Column(name = "max_results")
    @Builder.Default
    private Integer maxResults = 5;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "deleted")
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
