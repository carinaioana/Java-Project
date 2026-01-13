package com.example.lab4.entity;

import lombok.*;
import jakarta.persistence.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "courses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"instructor", "pack", "students"})
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CourseType type;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String abbr;

    @Column(nullable = false)
    private String name;

    // @ManyToOne association with Instructor
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instructor_id")
    private Instructor instructor;

    // @ManyToOne association with Pack
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pack_id")
    private Pack pack;

    @Column(name = "group_count")
    private Integer groupCount;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToMany(mappedBy = "courses")
    private Set<Student> students = new HashSet<>();

    // Entity Lifecycle Callbacks
    @PrePersist
    public void prePersist() {
        System.out.println("Course is about to be persisted: " + this.name);
    }

    @PostPersist
    public void postPersist() {
        System.out.println("Course has been persisted with ID: " + this.id);
    }

    @PreUpdate
    public void preUpdate() {
        System.out.println("Course is about to be updated: " + this.name);
    }

    @PreRemove
    public void preRemove() {
        System.out.println("Course is about to be removed: " + this.name);
    }
}

