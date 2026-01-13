package com.example.lab4.repository;

import com.example.lab4.entity.Student;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {


    Optional<Student> findByCode(String code);

    // DERIVED QUERY - spring generates SQL automatically
    List<Student> findByYear(Integer year);

    // JPQL QUERY - custom query string
    @Query("SELECT s FROM Student s WHERE s.email LIKE %:domain")
    List<Student> findByEmailDomain(@Param("domain") String domain);
    // Hibernate generates: SELECT * FROM students WHERE email LIKE '%gmail.com'

    @Query("SELECT s FROM Student s JOIN s.courses c WHERE c.id = :courseId")
    List<Student> findStudentsEnrolledInCourse(@Param("courseId") Long courseId);

    // TRANSACTIONAL QUERY - changes data (INSERT/UPDATE/DELETE)
    @Modifying
    @Transactional
    @Query("UPDATE Student s SET s.year = :newYear WHERE s.year = :oldYear")
    int promoteStudents(@Param("oldYear") Integer oldYear, @Param("newYear") Integer newYear);

    @Modifying
    @Transactional
    @Query("DELETE FROM Student s WHERE s.year > :maxYear")
    int deleteStudentsByYear(@Param("maxYear") Integer maxYear);

}