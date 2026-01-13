package com.example.lab4.service;

import com.example.lab4.entity.Course;
import com.example.lab4.entity.CourseType;
import com.example.lab4.repository.CourseRepository;
import com.example.lab4.specification.CourseSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CourseService {

    private final CourseRepository courseRepository;

    @Transactional
    public void createCourse(Course course) {
        courseRepository.save(course);
    }

    public Optional<Course> findById(Long id) {
        return courseRepository.findById(id);
    }
    public Optional<Course> findByCode(String code) {
        return courseRepository.findByCode(code);
    }

    public List<Course> findAll() {
        return courseRepository.findAll();
    }

    // Using Specifications for dynamic queries
    public Page<Course> searchCourses(CourseType type, Integer year, Long instructorId,
                                      String keyword, int page, int size) {
        Specification<Course> spec = Specification.where(CourseSpecifications.hasType(type))
                .and(CourseSpecifications.hasYear(year))
                .and(CourseSpecifications.hasInstructor(instructorId))
                .and(CourseSpecifications.nameContains(keyword));

        Pageable pageable = PageRequest.of(page, size, Sort.by("name"));
        return courseRepository.findAll(spec, pageable);
    }

    public List<Course> findByType(CourseType type) {
        return courseRepository.findByType(type);
    }

    @Transactional
    public Course updateCourse(Course course) {
        return courseRepository.save(course);
    }

    @Transactional
    public void deleteCourse(Long id) {
        courseRepository.deleteById(id);
    }

    public List<Course> findOptionalCoursesByYear(Integer year) {
        return courseRepository.findOptionalCoursesByYear(year);
    }
}