package com.example.lab4.repository;

import com.example.lab4.entity.Instructor;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

@Repository
public interface InstructorRepository extends JpaRepository<Instructor, Long> {

    // derived query
    Optional<Instructor> findByEmail(String email);

    List<Instructor> findByNameContainingIgnoreCase(String name);

    // JPQL query with SIZE function
    @Query("SELECT i FROM Instructor i WHERE SIZE(i.courses) > :minCourses")
    List<Instructor> findInstructorsWithMinimumCourses(@Param("minCourses") int minCourses);

    // JPQL with JOIN FETCH to avoid N+1 problem
    @Query("SELECT i FROM Instructor i LEFT JOIN FETCH i.courses WHERE i.id = :id")
    Optional<Instructor> findByIdWithCourses(@Param("id") Long id);

    // Pagination support
    Page<Instructor> findAll(Pageable pageable);
}

