package com.chapt003.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "tb_high_school_enrollment_plan")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TbEnrollmentPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "school_id")
    private Long schoolId;

    @Column(name = "school_code", length = 50)
    private String schoolCode;

    @Column(name = "school_name", length = 100)
    private String schoolName;

    @Column(name = "plan_year", length = 10)
    private String planYear;

    @Column(name = "enrollment_quota")
    private Integer enrollmentQuota;

    @Column(name = "allocation_quota")
    private Integer allocationQuota;

    @Column(name = "city_code", length = 20)
    private String cityCode;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getSchoolId() { return schoolId; }
    public void setSchoolId(Long schoolId) { this.schoolId = schoolId; }
    public String getSchoolCode() { return schoolCode; }
    public void setSchoolCode(String schoolCode) { this.schoolCode = schoolCode; }
    public String getSchoolName() { return schoolName; }
    public void setSchoolName(String schoolName) { this.schoolName = schoolName; }
    public String getPlanYear() { return planYear; }
    public void setPlanYear(String planYear) { this.planYear = planYear; }
    public Integer getEnrollmentQuota() { return enrollmentQuota; }
    public void setEnrollmentQuota(Integer enrollmentQuota) { this.enrollmentQuota = enrollmentQuota; }
    public Integer getAllocationQuota() { return allocationQuota; }
    public void setAllocationQuota(Integer allocationQuota) { this.allocationQuota = allocationQuota; }
    public String getCityCode() { return cityCode; }
    public void setCityCode(String cityCode) { this.cityCode = cityCode; }
}
