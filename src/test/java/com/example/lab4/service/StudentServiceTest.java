package com.example.lab4.service;

import com.example.lab4.entity.Student;
import com.example.lab4.repository.StudentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StudentServiceTest { // Changed to public class

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentService studentService;

    @Test
    void createStudent_HappyPath() {
        Student inputStudent = new Student();
        inputStudent.setName("Alice");
        inputStudent.setEmail("alice@test.com");

        Student savedStudent = new Student();
        savedStudent.setId(1L);
        savedStudent.setName("Alice");
        savedStudent.setEmail("alice@test.com");

        when(studentRepository.save(any(Student.class))).thenReturn(savedStudent);

        Student result = studentService.createStudent(inputStudent);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Alice", result.getName());
        verify(studentRepository, times(1)).save(inputStudent);
    }

    @Test
    void findById_ErrorPath_StudentNotFound() {
        Long nonExistentId = 999L;
        when(studentRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            studentService.findById(nonExistentId);
        });

        assertEquals("Student not found with id: 999", exception.getMessage());
        verify(studentRepository, times(1)).findById(nonExistentId);
    }
}