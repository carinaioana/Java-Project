package com.example.lab4.service;

import com.example.lab4.entity.Student;
import com.example.lab4.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudentService {

    private final StudentRepository studentRepository;

    @Transactional
    public Student createStudent(Student student) {
        return studentRepository.save(student);
    }

    public Student findById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + id));

    }
    public Optional<Student> findByCode(String code) {
        return studentRepository.findByCode(code);
    }
    public List<Student> findAll() {
        return studentRepository.findAll();
    }

    public Page<Student> findAllPaginated(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return studentRepository.findAll(pageable);
    }

    public List<Student> findByYear(Integer year) {
        return studentRepository.findByYear(year);
    }

    @Transactional
    public Student updateStudent(Long id, Student student) {
        return studentRepository.save(student);
    }

    @Transactional
    public void deleteStudent(Long id) {
        studentRepository.deleteById(id);
    }

    @Transactional
    public int promoteStudents(Integer oldYear, Integer newYear) {
        return studentRepository.promoteStudents(oldYear, newYear);
    }
    public long count() {
        return studentRepository.count();
    }
}
