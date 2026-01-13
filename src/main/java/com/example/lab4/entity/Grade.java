package com.example.lab4.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties; // Import this
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "grades")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Grade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    // ignore the 'courses' list inside the Student object to prevent the loop
    @JsonIgnoreProperties({"courses", "password", "preferences"})
    private Student student;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    @JsonIgnoreProperties({"students", "instructor", "pack"})
    private Course course;

    @Column(nullable = false)
    private Double value;
}