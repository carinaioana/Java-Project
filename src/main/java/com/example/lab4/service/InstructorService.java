package com.example.lab4.service;

import com.example.lab4.entity.Instructor;
import com.example.lab4.repository.InstructorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class InstructorService {

    private final InstructorRepository instructorRepository;

    // Constructor injection (no @Autowired needed in modern Spring)
    public InstructorService(InstructorRepository instructorRepository) {
        this.instructorRepository = instructorRepository;
    }

    @Transactional
    public Instructor createInstructor(Instructor instructor) {
        return instructorRepository.save(instructor);
    }

    public Optional<Instructor> findById(Long id) {
        return instructorRepository.findById(id);
    }

    public List<Instructor> findAll() {
        return instructorRepository.findAll();
    }

    public Optional<Instructor> findByEmail(String email) {
        return instructorRepository.findByEmail(email);
    }

    public List<Instructor> findByNameContaining(String name) {
        return instructorRepository.findByNameContainingIgnoreCase(name);
    }

    @Transactional
    public Instructor updateInstructor(Instructor instructor) {
        return instructorRepository.save(instructor);
    }

    @Transactional
    public void deleteInstructor(Long id) {
        instructorRepository.deleteById(id);
    }

    public long count() {
        return instructorRepository.count();
    }

    public boolean existsById(Long id) {
        return instructorRepository.existsById(id);
    }
}
