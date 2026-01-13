package com.example.lab4.repository;

import com.example.lab4.entity.Course;
import com.example.lab4.entity.CourseType;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long>, JpaSpecificationExecutor<Course> {

    // derived queries
    Optional<Course> findByCode(String code);

    List<Course> findByType(CourseType type);

    List<Course> findByInstructorId(Long instructorId);

    List<Course> findByPackId(Long packId);

    // JPQL queries
    @Query("SELECT c FROM Course c WHERE c.pack.year = :year AND c.type = 'OPTIONAL'")
    List<Course> findOptionalCoursesByYear(@Param("year") Integer year);

    // TRANSACTIONAL MODIFYING QUERY
    @Modifying
    @Transactional
    @Query("UPDATE Course c SET c.groupCount = :groupCount WHERE c.id = :id")
    int updateGroupCount(@Param("id") Long id, @Param("groupCount") Integer groupCount);

    @Modifying
    @Transactional
    @Query("DELETE FROM Course c WHERE c.instructor.id = :instructorId")
    int deleteByInstructorId(@Param("instructorId") Long instructorId);


    // method for Criteria API (will be used in Service)
    @Query("SELECT c FROM Course c WHERE " +
            "(:type IS NULL OR c.type = :type) AND " +
            "(:year IS NULL OR c.pack.year = :year)")
    Page<Course> findByCriteria(@Param("type") CourseType type,
                                @Param("year") Integer year,
                                Pageable pageable);
}
