package com.example.lab4.repository;

import com.example.lab4.entity.StudentPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentPreferenceRepository extends JpaRepository<StudentPreference, Long> {

    List<StudentPreference> findByStudentId(Long studentId);

    boolean existsByStudentIdAndCourseId(Long studentId, Long courseId);

    // fetch all preferences for a specific pack
    List<StudentPreference> findByPackName(String packName);
}