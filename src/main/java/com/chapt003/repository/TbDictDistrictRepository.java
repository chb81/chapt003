package com.chapt003.repository;

import com.chapt003.entity.TbDictDistrict;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TbDictDistrictRepository extends JpaRepository<TbDictDistrict, Integer> {

    List<TbDictDistrict> findByLevel(Integer level);

    List<TbDictDistrict> findByProvinceCode(Integer provinceCode);

    Optional<TbDictDistrict> findByCityCode(Integer cityCode);
}
