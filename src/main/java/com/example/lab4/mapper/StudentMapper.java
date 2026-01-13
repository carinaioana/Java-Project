package com.example.lab4.mapper;

import com.example.lab4.dto.StudentDTO;
import com.example.lab4.entity.Student;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class StudentMapper {

    // Entity -> DTO
    public StudentDTO toDTO(Student student) {
        if (student == null) {
            return null;
        }

        StudentDTO dto = new StudentDTO();
        dto.setId(student.getId());
        dto.setCode(student.getCode());
        dto.setName(student.getName());
        dto.setEmail(student.getEmail());
        dto.setYear(student.getYear());

        return dto;
    }

    // DTO -> Entity
    public Student toEntity(StudentDTO dto) {
        if (dto == null) {
            return null;
        }

        Student student = new Student();
        student.setId(dto.getId());
        student.setCode(dto.getCode());
        student.setName(dto.getName());
        student.setEmail(dto.getEmail());
        student.setYear(dto.getYear());

        return student;
    }

    // List<Entity> -> List<DTO>
    public List<StudentDTO> toDTOList(List<Student> students) {
        return students.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
