package com.chapt003.entity;

import com.chapt003.entity.enums.SchoolType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "schools")
@SQLDelete(sql = "UPDATE schools SET deleted = true WHERE id = ?")
@Where(clause = "deleted = false")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class School {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private SchoolType type;

    @Column(length = 50)
    private String city;

    @Column(length = 50)
    private String district;

    @Column(name = "admission_score_year1", precision = 5, scale = 2)
    private BigDecimal admissionScoreYear1;

    @Column(name = "admission_score_year2", precision = 5, scale = 2)
    private BigDecimal admissionScoreYear2;

    @Column(name = "admission_score_year3", precision = 5, scale = 2)
    private BigDecimal admissionScoreYear3;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String features;

    @Column(name = "enrollment_quota")
    private Integer enrollmentQuota;

    @Column(name = "applicant_count")
    private Integer applicantCount;

    @Column(length = 20)
    private String phone;

    @Column(length = 200)
    private String address;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SchoolType getType() {
        return type;
    }

    public void setType(SchoolType type) {
        this.type = type;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public BigDecimal getAdmissionScoreYear1() {
        return admissionScoreYear1;
    }

    public void setAdmissionScoreYear1(BigDecimal admissionScoreYear1) {
        this.admissionScoreYear1 = admissionScoreYear1;
    }

    public BigDecimal getAdmissionScoreYear2() {
        return admissionScoreYear2;
    }

    public void setAdmissionScoreYear2(BigDecimal admissionScoreYear2) {
        this.admissionScoreYear2 = admissionScoreYear2;
    }

    public BigDecimal getAdmissionScoreYear3() {
        return admissionScoreYear3;
    }

    public void setAdmissionScoreYear3(BigDecimal admissionScoreYear3) {
        this.admissionScoreYear3 = admissionScoreYear3;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFeatures() {
        return features;
    }

    public void setFeatures(String features) {
        this.features = features;
    }

    public Integer getEnrollmentQuota() {
        return enrollmentQuota;
    }

    public void setEnrollmentQuota(Integer enrollmentQuota) {
        this.enrollmentQuota = enrollmentQuota;
    }

    public Integer getApplicantCount() {
        return applicantCount;
    }

    public void setApplicantCount(Integer applicantCount) {
        this.applicantCount = applicantCount;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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
