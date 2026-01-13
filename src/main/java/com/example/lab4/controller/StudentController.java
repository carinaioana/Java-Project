package com.example.lab4.controller;

import com.example.lab4.dto.StudentDTO;
import com.example.lab4.entity.Student;
import com.example.lab4.mapper.StudentMapper;
import com.example.lab4.service.StudentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/students")
@Tag(name = "Students")
public class StudentController {

    private final StudentService studentService;
    private final StudentMapper studentMapper;

    public StudentController(StudentService studentService, StudentMapper studentMapper) {
        this.studentService = studentService;
        this.studentMapper = studentMapper;
    }

    @PostMapping
    public ResponseEntity<StudentDTO> createStudent(@RequestBody StudentDTO studentDTO) {
        Student student = studentMapper.toEntity(studentDTO);
        Student createdStudent = studentService.createStudent(student);
        StudentDTO responseDTO = studentMapper.toDTO(createdStudent);
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<StudentDTO>> getAllStudents() {
        List<Student> students = studentService.findAll();
        List<StudentDTO> studentDTOs = studentMapper.toDTOList(students);
        return ResponseEntity.ok(studentDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentDTO> getStudentById(@PathVariable Long id) {
        Student student = studentService.findById(id);
        StudentDTO studentDTO = studentMapper.toDTO(student);
        return ResponseEntity.ok(studentDTO);
    }

    @GetMapping("/year/{year}")
    public ResponseEntity<List<StudentDTO>> getStudentsByYear(@PathVariable Integer year) {
        List<Student> students = studentService.findByYear(year);
        List<StudentDTO> studentDTOs = studentMapper.toDTOList(students);
        return ResponseEntity.ok(studentDTOs);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudentDTO> updateStudent(
            @PathVariable Long id,
            @RequestBody StudentDTO studentDTO) {
        Student studentDetails = studentMapper.toEntity(studentDTO);
        Student updatedStudent = studentService.updateStudent(id, studentDetails);
        StudentDTO responseDTO = studentMapper.toDTO(updatedStudent);
        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // method-level security
    public ResponseEntity<Map<String, String>> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Student deleted successfully");
        response.put("id", id.toString());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> getStudentCount() {
        long count = studentService.count();
        Map<String, Long> response = new HashMap<>();
        response.put("count", count);
        return ResponseEntity.ok(response);
    }
}