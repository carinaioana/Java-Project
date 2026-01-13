package com.example.lab4.repository;

import com.example.lab4.entity.Role;
import com.example.lab4.entity.Student;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // Force Testcontainers
public class StudentRepositoryIntegrationTest {

    // 1. Define the Container (matches your real DB version)
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    // 2. Connect Spring Boot to the Container
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private StudentRepository studentRepository;

    @Test
    void testSchemaAndCrudOperations() {
        Student student = new Student();
        student.setName("Container Student");
        student.setEmail("container@test.com");
        student.setPassword("password123");
        student.setRole(Role.STUDENT);
        student.setCode("TC-TEST-001");
        student.setYear(2);

        Student saved = studentRepository.save(student);

        // If saved.getId() is not null, the Schema was created and Insert worked
        assertThat(saved.getId()).isNotNull();

        Student found = studentRepository.findById(saved.getId()).orElse(null);
        assertThat(found).isNotNull();
        assertThat(found.getEmail()).isEqualTo("container@test.com");
    }
}