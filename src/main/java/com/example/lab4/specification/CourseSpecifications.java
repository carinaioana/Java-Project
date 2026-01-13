package com.example.lab4.specification;

import com.example.lab4.entity.Course;
import com.example.lab4.entity.CourseType;
import org.springframework.data.jpa.domain.Specification;

public class CourseSpecifications {

    public static Specification<Course> hasType(CourseType type) {
        return (root, query, cb) ->
                type == null ? cb.conjunction() : cb.equal(root.get("type"), type);
    }

    public static Specification<Course> hasYear(Integer year) {
        return (root, query, cb) ->
                year == null ? cb.conjunction() : cb.equal(root.get("pack").get("year"), year);
    }

    public static Specification<Course> hasInstructor(Long instructorId) {
        return (root, query, cb) ->
                instructorId == null ? cb.conjunction() : cb.equal(root.get("instructor").get("id"), instructorId);
    }

    public static Specification<Course> nameContains(String keyword) {
        return (root, query, cb) ->
                keyword == null ? cb.conjunction() : cb.like(cb.lower(root.get("name")), "%" + keyword.toLowerCase() + "%");
    }
}