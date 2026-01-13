package com.example.lab4.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "students")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@ToString(exclude = "courses")
@PrimaryKeyJoinColumn(name = "id")
public class Student extends Person {

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private Integer year;

    @ManyToMany
    @JoinTable(
            name = "student_courses",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    private Set<Course> courses = new HashSet<>();

    public Student(String code, String name, String email, String password, Integer year) {
        super(name, email, password, Role.STUDENT);
        this.code = code;
        this.year = year;
    }
}