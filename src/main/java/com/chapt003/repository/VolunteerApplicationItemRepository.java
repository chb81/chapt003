package com.chapt003.repository;

import com.chapt003.entity.VolunteerApplicationItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VolunteerApplicationItemRepository extends JpaRepository<VolunteerApplicationItem, Long> {

    List<VolunteerApplicationItem> findByVolunteerApplicationId(Long volunteerApplicationId);

    List<VolunteerApplicationItem> findByVolunteerApplicationIdOrderByPriorityAsc(Long volunteerApplicationId);

    Optional<VolunteerApplicationItem> findByVolunteerApplicationIdAndPriority(Long volunteerApplicationId, Integer priority);

    Optional<VolunteerApplicationItem> findByVolunteerApplicationIdAndSchoolId(Long volunteerApplicationId, Long schoolId);

    boolean existsByVolunteerApplicationIdAndSchoolId(Long volunteerApplicationId, Long schoolId);

    @Query("SELECT COUNT(vai) FROM VolunteerApplicationItem vai WHERE vai.volunteerApplication.id = :volunteerApplicationId")
    long countByVolunteerApplicationId(@Param("volunteerApplicationId") Long volunteerApplicationId);

    void deleteByVolunteerApplicationIdAndPriority(Long volunteerApplicationId, Integer priority);

    void deleteByVolunteerApplicationIdAndSchoolId(Long volunteerApplicationId, Long schoolId);
}
