package com.example.lab4.entity;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "student_preferences")
@Data
@NoArgsConstructor
public class StudentPreference {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    @Column(nullable = false)
    private Integer preferenceRank;

    @Column(nullable = false)
    private String packName;

    @Version  // For ETag
    private Long version;
}