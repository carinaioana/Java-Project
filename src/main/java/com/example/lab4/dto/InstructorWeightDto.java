package com.example.lab4.dto;

import lombok.Data;
import java.util.List;

@Data
public class InstructorWeightDto {
    private String optionalCourseAbbr;
    private List<WeightPair> weights;

    @Data
    public static class WeightPair {
        private String compulsoryCourseAbbr;
        private Double percentage; // e.g., 100.0
    }
}