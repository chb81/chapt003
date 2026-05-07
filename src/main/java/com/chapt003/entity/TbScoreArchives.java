package com.chapt003.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "tb_score_archives")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TbScoreArchives {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "score")
    private Integer score;

    @Column(name = "ranking")
    private Integer ranking;

    @Column(name = "exay_year", length = 10)
    private String exayYear;

    @Column(name = "city_code", length = 20)
    private String cityCode;

    @Column(name = "students_num")
    private Integer studentsNum;

    @Column(name = "students_total_num")
    private Integer studentsTotalNum;

    @Column(name = "stundents_percentage", precision = 10, scale = 8)
    private BigDecimal studentsPercentage;

    @Column(name = "stundents_total_percentage", precision = 10, scale = 8)
    private BigDecimal studentsTotalPercentage;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }
    public Integer getRanking() { return ranking; }
    public void setRanking(Integer ranking) { this.ranking = ranking; }
    public String getExayYear() { return exayYear; }
    public void setExayYear(String exayYear) { this.exayYear = exayYear; }
    public String getCityCode() { return cityCode; }
    public void setCityCode(String cityCode) { this.cityCode = cityCode; }
    public Integer getStudentsNum() { return studentsNum; }
    public void setStudentsNum(Integer studentsNum) { this.studentsNum = studentsNum; }
    public Integer getStudentsTotalNum() { return studentsTotalNum; }
    public void setStudentsTotalNum(Integer studentsTotalNum) { this.studentsTotalNum = studentsTotalNum; }
    public BigDecimal getStudentsPercentage() { return studentsPercentage; }
    public void setStudentsPercentage(BigDecimal studentsPercentage) { this.studentsPercentage = studentsPercentage; }
    public BigDecimal getStudentsTotalPercentage() { return studentsTotalPercentage; }
    public void setStudentsTotalPercentage(BigDecimal studentsTotalPercentage) { this.studentsTotalPercentage = studentsTotalPercentage; }
}
