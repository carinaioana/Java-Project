package com.example.lab4.dto;

import java.io.Serializable;
import lombok.Data;

@Data
public class GradeEvent implements Serializable {
    private String studentCode;
    private String courseCode;
    private Double grade;
}