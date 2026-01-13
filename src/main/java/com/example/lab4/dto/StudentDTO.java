package com.example.lab4.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentDTO {
    private Long id;
    private String code;
    private String name;
    private String email;
    private Integer year;
}