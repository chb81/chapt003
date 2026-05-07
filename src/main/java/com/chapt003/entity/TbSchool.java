package com.chapt003.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_school")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TbSchool {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "school_code", length = 50, unique = true)
    private String schoolCode;

    @Column(name = "school_name", length = 100)
    private String schoolName;

    @Column(name = "school_type", length = 20)
    private String schoolType;

    @Column(name = "school_nature", length = 20)
    private String schoolNature;

    @Column(name = "area_code", length = 20)
    private String areaCode;

    @Column(name = "city", length = 50)
    private String city;

    @Column(name = "district", length = 50)
    private String district;

    @Column(name = "school_rank")
    private Integer schoolRank;

    @Column(name = "school_remark", columnDefinition = "TEXT")
    private String schoolRemark;

    @Column(name = "admission_score_year1", precision = 5, scale = 2)
    private BigDecimal admissionScoreYear1;

    @Column(name = "admission_score_year2", precision = 5, scale = 2)
    private BigDecimal admissionScoreYear2;

    @Column(name = "admission_score_year3", precision = 5, scale = 2)
    private BigDecimal admissionScoreYear3;

    @Column(name = "enrollment_quota")
    private Integer enrollmentQuota;

    @Column(name = "applicant_count")
    private Integer applicantCount;

    @Column(length = 20)
    private String phone;

    @Column(length = 200)
    private String address;

    @Column(name = "school_level", length = 30)
    private String schoolLevel;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String features;

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

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getSchoolCode() { return schoolCode; }
    public void setSchoolCode(String schoolCode) { this.schoolCode = schoolCode; }
    public String getSchoolName() { return schoolName; }
    public void setSchoolName(String schoolName) { this.schoolName = schoolName; }
    public String getSchoolType() { return schoolType; }
    public void setSchoolType(String schoolType) { this.schoolType = schoolType; }
    public String getSchoolNature() { return schoolNature; }
    public void setSchoolNature(String schoolNature) { this.schoolNature = schoolNature; }
    public String getAreaCode() { return areaCode; }
    public void setAreaCode(String areaCode) { this.areaCode = areaCode; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }
    public Integer getSchoolRank() { return schoolRank; }
    public void setSchoolRank(Integer schoolRank) { this.schoolRank = schoolRank; }
    public String getSchoolRemark() { return schoolRemark; }
    public void setSchoolRemark(String schoolRemark) { this.schoolRemark = schoolRemark; }
    public BigDecimal getAdmissionScoreYear1() { return admissionScoreYear1; }
    public void setAdmissionScoreYear1(BigDecimal admissionScoreYear1) { this.admissionScoreYear1 = admissionScoreYear1; }
    public BigDecimal getAdmissionScoreYear2() { return admissionScoreYear2; }
    public void setAdmissionScoreYear2(BigDecimal admissionScoreYear2) { this.admissionScoreYear2 = admissionScoreYear2; }
    public BigDecimal getAdmissionScoreYear3() { return admissionScoreYear3; }
    public void setAdmissionScoreYear3(BigDecimal admissionScoreYear3) { this.admissionScoreYear3 = admissionScoreYear3; }
    public Integer getEnrollmentQuota() { return enrollmentQuota; }
    public void setEnrollmentQuota(Integer enrollmentQuota) { this.enrollmentQuota = enrollmentQuota; }
    public Integer getApplicantCount() { return applicantCount; }
    public void setApplicantCount(Integer applicantCount) { this.applicantCount = applicantCount; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getSchoolLevel() { return schoolLevel; }
    public void setSchoolLevel(String schoolLevel) { this.schoolLevel = schoolLevel; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getFeatures() { return features; }
    public void setFeatures(String features) { this.features = features; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public Boolean getDeleted() { return deleted; }
    public void setDeleted(Boolean deleted) { this.deleted = deleted; }

    public String getName() { return schoolName; }

    public String getTypeName() {
        if (schoolRank != null) {
            if (schoolRank <= 5) return "KEY_HIGH_SCHOOL";
            if (schoolRank <= 15) return "REGULAR_HIGH_SCHOOL";
        }
        if ("重点".equals(schoolNature) || "省级示范性".equals(schoolNature)) return "KEY_HIGH_SCHOOL";
        if ("普通".equals(schoolNature)) return "REGULAR_HIGH_SCHOOL";
        if ("职业".equals(schoolNature) || "中职".equals(schoolNature)) return "VOCATIONAL_HIGH_SCHOOL";
        return "REGULAR_HIGH_SCHOOL";
    }
}
