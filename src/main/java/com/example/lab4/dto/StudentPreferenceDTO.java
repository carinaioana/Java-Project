package com.example.lab4.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement  // for XML support
public class StudentPreferenceDTO {

    private Long id;

    @NotNull
    @Positive
    private Long studentId;

    @NotNull
    @Positive
    private Long courseId;

    @NotNull
    @Min(1)
    private Integer preferenceRank;

    @NotBlank
    private String packName;
}