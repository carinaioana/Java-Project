package com.example.lab4.repository;

import com.example.lab4.entity.InstructorPreference;
import com.example.lab4.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface InstructorPreferenceRepository extends JpaRepository<InstructorPreference, Long> {
    // Find all weights defined for a specific optional course
    List<InstructorPreference> findByOptionalCourse(Course optionalCourse);
}