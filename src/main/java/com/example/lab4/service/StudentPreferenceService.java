package com.example.lab4.service;

import com.example.lab4.dto.StudentPreferenceDTO;
import com.example.lab4.entity.Course;
import com.example.lab4.entity.Student;
import com.example.lab4.entity.StudentPreference;
import com.example.lab4.exception.PreferenceException;
import com.example.lab4.repository.CourseRepository;
import com.example.lab4.repository.StudentPreferenceRepository;
import com.example.lab4.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentPreferenceService {

    private final StudentPreferenceRepository repository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    public StudentPreference create(StudentPreferenceDTO dto) {
        // basic validation
        Student student = studentRepository.findById(dto.getStudentId())
                .orElseThrow(() -> new PreferenceException("Student not found"));

        Course course = courseRepository.findById(dto.getCourseId())
                .orElseThrow(() -> new PreferenceException("Course not found"));

        if (repository.existsByStudentIdAndCourseId(dto.getStudentId(), dto.getCourseId())) {
            throw new PreferenceException("Preference already exists");
        }

        // create and save
        StudentPreference pref = new StudentPreference();
        pref.setStudent(student);
        pref.setCourse(course);
        pref.setPreferenceRank(dto.getPreferenceRank());
        pref.setPackName(dto.getPackName());

        return repository.save(pref);
    }

    public StudentPreference findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new PreferenceException("Preference not found"));
    }

    public List<StudentPreference> findAll() {
        return repository.findAll();
    }

    public List<StudentPreference> findByStudentId(Long studentId) {
        return repository.findByStudentId(studentId);
    }

    public StudentPreference update(Long id, StudentPreferenceDTO dto) {
        StudentPreference pref = findById(id);
        pref.setPreferenceRank(dto.getPreferenceRank());
        pref.setPackName(dto.getPackName());
        return repository.save(pref);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}