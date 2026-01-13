package com.example.lab4.repository;

import com.example.lab4.entity.Pack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PackRepository extends JpaRepository<Pack, Long> {

    // derived queries
    List<Pack> findByYear(Integer year);

    List<Pack> findByYearAndSemester(Integer year, String semester);

    Optional<Pack> findByName(String name);

    // JPQL with JOIN FETCH
    @Query("SELECT p FROM Pack p LEFT JOIN FETCH p.courses WHERE p.id = :id")
    Optional<Pack> findByIdWithCourses(@Param("id") Long id);

    @Query("SELECT p FROM Pack p WHERE p.year = :year ORDER BY p.semester")
    List<Pack> findPacksByYearOrdered(@Param("year") Integer year);
}
