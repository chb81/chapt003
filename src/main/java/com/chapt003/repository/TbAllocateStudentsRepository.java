package com.chapt003.repository;

import com.chapt003.entity.TbAllocateStudents;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TbAllocateStudentsRepository extends JpaRepository<TbAllocateStudents, Long> {

    List<TbAllocateStudents> findByHighSchoolCodeAndExayYear(String highSchoolCode, String exayYear);

    List<TbAllocateStudents> findByMiddleSchoolCodeAndExayYear(String middleSchoolCode, String exayYear);

    @Query("SELECT SUM(a.allocateStudentsNum) FROM TbAllocateStudents a WHERE a.highSchoolCode = :highSchoolCode AND a.exayYear = :year")
    Integer getTotalAllocationByHighSchoolAndYear(@Param("highSchoolCode") String highSchoolCode, @Param("year") String year);

    @Query("SELECT a FROM TbAllocateStudents a WHERE a.middleSchoolCode = :middleSchoolCode AND a.highSchoolCode = :highSchoolCode AND a.exayYear = :year")
    List<TbAllocateStudents> findByMiddleAndHighSchoolAndYear(@Param("middleSchoolCode") String middleSchoolCode, @Param("highSchoolCode") String highSchoolCode, @Param("year") String year);
}
