package com.example.lab4.controller;

import com.example.lab4.entity.*;
import com.example.lab4.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

@RestController
@RequestMapping("/api/grades")
@RequiredArgsConstructor
public class GradeController {

    private final GradeRepository gradeRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    @GetMapping
    public List<Grade> getAllGrades() {
        return gradeRepository.findAll();
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadCsv(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) return ResponseEntity.badRequest().body("File is empty");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;

            while ((line = reader.readLine()) != null) {
                // format: studentCode,courseCode,grade
                String[] data = line.split(",");
                if (data.length < 3) continue;

                String sCode = data[0].trim();
                String cCode = data[1].trim();
                Double val = Double.parseDouble(data[2].trim());

                Student s = studentRepository.findByCode(sCode).orElse(null);
                Course c = courseRepository.findByCode(cCode).orElse(null);

                if (s != null && c != null && c.getType() == CourseType.COMPULSORY) {
                    Grade g = new Grade(null, s, c, val);
                    gradeRepository.save(g);
                }
            }
            return ResponseEntity.ok("CSV Processed Successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }
}