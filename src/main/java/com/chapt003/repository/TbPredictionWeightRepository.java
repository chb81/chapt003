package com.chapt003.repository;

import com.chapt003.entity.TbPredictionWeight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TbPredictionWeightRepository extends JpaRepository<TbPredictionWeight, Long> {

    List<TbPredictionWeight> findByYearAndCityCode(String year, String cityCode);

    Optional<TbPredictionWeight> findByWeightNameAndYearAndCityCode(String weightName, String year, String cityCode);

    List<TbPredictionWeight> findByCityCode(String cityCode);
}
