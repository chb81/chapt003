package com.chapt003.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "tb_allocate_students")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TbAllocateStudents {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "middle_school_code", length = 50)
    private String middleSchoolCode;

    @Column(name = "high_school_code", length = 50)
    private String highSchoolCode;

    @Column(name = "allocate_students_num")
    private Integer allocateStudentsNum;

    @Column(name = "exay_year", length = 10)
    private String exayYear;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getMiddleSchoolCode() { return middleSchoolCode; }
    public void setMiddleSchoolCode(String middleSchoolCode) { this.middleSchoolCode = middleSchoolCode; }
    public String getHighSchoolCode() { return highSchoolCode; }
    public void setHighSchoolCode(String highSchoolCode) { this.highSchoolCode = highSchoolCode; }
    public Integer getAllocateStudentsNum() { return allocateStudentsNum; }
    public void setAllocateStudentsNum(Integer allocateStudentsNum) { this.allocateStudentsNum = allocateStudentsNum; }
    public String getExayYear() { return exayYear; }
    public void setExayYear(String exayYear) { this.exayYear = exayYear; }
}
