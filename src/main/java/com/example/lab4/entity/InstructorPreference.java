package com.example.lab4.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "instructor_preferences")
@Data
@NoArgsConstructor
public class InstructorPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "optional_course_id", nullable = false)
    private Course optionalCourse;

    @ManyToOne
    @JoinColumn(name = "compulsory_course_id", nullable = false)
    private Course compulsoryCourse;

    @Column(nullable = false)
    private Double weight;

    public InstructorPreference(Course optionalCourse, Course compulsoryCourse, Double weight) {
        this.optionalCourse = optionalCourse;
        this.compulsoryCourse = compulsoryCourse;
        this.weight = weight;
    }
}