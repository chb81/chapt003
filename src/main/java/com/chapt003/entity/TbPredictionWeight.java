package com.chapt003.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "tb_prediction_weight")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TbPredictionWeight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "weight_name", length = 100)
    private String weightName;

    @Column(name = "weight_value", precision = 5, scale = 4)
    private BigDecimal weightValue;

    @Column(name = "year", length = 10)
    private String year;

    @Column(name = "city_code", length = 20)
    private String cityCode;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getWeightName() { return weightName; }
    public void setWeightName(String weightName) { this.weightName = weightName; }
    public BigDecimal getWeightValue() { return weightValue; }
    public void setWeightValue(BigDecimal weightValue) { this.weightValue = weightValue; }
    public String getYear() { return year; }
    public void setYear(String year) { this.year = year; }
    public String getCityCode() { return cityCode; }
    public void setCityCode(String cityCode) { this.cityCode = cityCode; }
}
