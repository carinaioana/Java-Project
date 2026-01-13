package com.example.lab4.controller;

import com.example.lab4.dto.InstructorWeightDto;
import com.example.lab4.entity.Course;
import com.example.lab4.entity.InstructorPreference;
import com.example.lab4.repository.CourseRepository;
import com.example.lab4.repository.InstructorPreferenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/preferences")
@RequiredArgsConstructor
public class InstructorPreferenceController {

    private final InstructorPreferenceRepository preferenceRepository;
    private final CourseRepository courseRepository;

    @PostMapping("/set")
    public ResponseEntity<String> setPreferences(@RequestBody InstructorWeightDto dto) {
        // 1. Find the optional course
        Course optionalCourse = courseRepository.findAll().stream()
                .filter(c -> c.getAbbr().equals(dto.getOptionalCourseAbbr()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Optional Course not found"));

        // 2. Clear old preferences for this course (to avoid duplicates if updated)
        List<InstructorPreference> existing = preferenceRepository.findByOptionalCourse(optionalCourse);
        preferenceRepository.deleteAll(existing);

        // 3. Save new preferences
        for (InstructorWeightDto.WeightPair pair : dto.getWeights()) {
            Course compulsoryCourse = courseRepository.findAll().stream()
                    .filter(c -> c.getAbbr().equals(pair.getCompulsoryCourseAbbr()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Compulsory Course " + pair.getCompulsoryCourseAbbr() + " not found"));

            InstructorPreference pref = new InstructorPreference(optionalCourse, compulsoryCourse, pair.getPercentage());
            preferenceRepository.save(pref);
        }

        return ResponseEntity.ok("Preferences saved for " + dto.getOptionalCourseAbbr());
    }
}