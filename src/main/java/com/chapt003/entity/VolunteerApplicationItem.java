package com.chapt003.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "volunteer_application_items")
@SQLDelete(sql = "UPDATE volunteer_application_items SET deleted = true WHERE id = ?")
@Where(clause = "deleted = false")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VolunteerApplicationItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "volunteer_application_id", nullable = false)
    private VolunteerApplication volunteerApplication;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id", nullable = false)
    private TbSchool school;

    @Column(nullable = false)
    private Integer priority;

    @Column(name = "admission_probability", precision = 5, scale = 2)
    private BigDecimal admissionProbability;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted", nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0")
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public VolunteerApplication getVolunteerApplication() {
        return volunteerApplication;
    }

    public void setVolunteerApplication(VolunteerApplication volunteerApplication) {
        this.volunteerApplication = volunteerApplication;
    }

    public TbSchool getSchool() {
        return school;
    }

    public void setSchool(TbSchool school) {
        this.school = school;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public BigDecimal getAdmissionProbability() {
        return admissionProbability;
    }

    public void setAdmissionProbability(BigDecimal admissionProbability) {
        this.admissionProbability = admissionProbability;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }
}
