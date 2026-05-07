package com.chapt003.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "tb_dict_district")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TbDictDistrict {

    @Id
    @Column(name = "f_city_code")
    private Integer cityCode;

    @Column(name = "f_province_code")
    private Integer provinceCode;

    @Column(name = "f_county_code")
    private Integer countyCode;

    @Column(name = "f_level")
    private Integer level;

    @Column(name = "f_province_name", length = 50)
    private String provinceName;

    @Column(name = "f_city_name", length = 50)
    private String cityName;

    @Column(name = "f_county_name", length = 50)
    private String countyName;

    public Integer getCityCode() { return cityCode; }
    public void setCityCode(Integer cityCode) { this.cityCode = cityCode; }
    public Integer getProvinceCode() { return provinceCode; }
    public void setProvinceCode(Integer provinceCode) { this.provinceCode = provinceCode; }
    public Integer getCountyCode() { return countyCode; }
    public void setCountyCode(Integer countyCode) { this.countyCode = countyCode; }
    public Integer getLevel() { return level; }
    public void setLevel(Integer level) { this.level = level; }
    public String getProvinceName() { return provinceName; }
    public void setProvinceName(String provinceName) { this.provinceName = provinceName; }
    public String getCityName() { return cityName; }
    public void setCityName(String cityName) { this.cityName = cityName; }
    public String getCountyName() { return countyName; }
    public void setCountyName(String countyName) { this.countyName = countyName; }
}
