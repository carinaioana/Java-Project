package com.example.lab4.controller;

import com.example.lab4.dto.StudentDTO;
import com.example.lab4.entity.Student;
import com.example.lab4.mapper.StudentMapper;
import com.example.lab4.service.StudentService;
import com.example.lab4.security.JwtAuthenticationFilter;
import com.example.lab4.security.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentController.class)
@AutoConfigureMockMvc(addFilters = false)
public class StudentControllerTest { // Changed to public class

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentService studentService;

    @MockBean
    private StudentMapper studentMapper;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    @MockBean
    private JwtService jwtService;
    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    void getStudentById_HappyPath() throws Exception {
        Long studentId = 1L;
        Student student = new Student();
        student.setId(studentId);
        student.setName("John Doe");

        StudentDTO studentDTO = new StudentDTO();
        studentDTO.setId(studentId);
        studentDTO.setName("John Doe");

        when(studentService.findById(studentId)).thenReturn(student);
        when(studentMapper.toDTO(any(Student.class))).thenReturn(studentDTO);

        mockMvc.perform(get("/api/students/{id}", studentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(studentId))
                .andExpect(jsonPath("$.name").value("John Doe"));
    }

    @Test
    void getStudentById_NotFound_ShouldReturn500() throws Exception {
        Long invalidId = 99L;
        when(studentService.findById(invalidId))
                .thenThrow(new RuntimeException("Student not found with id: " + invalidId));

        mockMvc.perform(get("/api/students/{id}", invalidId)
                        .contentType(MediaType.APPLICATION_JSON))
                // This expects your GlobalExceptionHandler to catch RuntimeException
                .andExpect(status().isInternalServerError());
    }
}