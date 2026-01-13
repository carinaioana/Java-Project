package com.example.lab4.controller;

import com.example.lab4.dto.StudentPreferenceDTO;
import com.example.lab4.entity.StudentPreference;
import com.example.lab4.service.StudentPreferenceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.util.List;

@RestController
@RequestMapping("/api/preferences")
@RequiredArgsConstructor
public class StudentPreferenceController {

    private final StudentPreferenceService service;

    @PostMapping
    public ResponseEntity<StudentPreferenceDTO> create(@Valid @RequestBody StudentPreferenceDTO dto) {
        StudentPreference pref = service.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(pref));
    }

    @GetMapping
    public List<StudentPreferenceDTO> getAll() {
        return service.findAll().stream().map(this::toDTO).toList();
    }

    @GetMapping(value = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<StudentPreferenceDTO> getById(@PathVariable Long id, WebRequest request) {
        StudentPreference pref = service.findById(id);

        // ETag = just the version number
        //generated from the entity's version
        String etag = "\"" + pref.getVersion() + "\"";

        // If-None-Match check
        // compares the client's If-None-Match header with the current ETag
        if (request.checkNotModified(etag)) {
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
        }

        return ResponseEntity.ok().eTag(etag).body(toDTO(pref));
    }

    @GetMapping("/student/{studentId}")
    public List<StudentPreferenceDTO> getByStudent(@PathVariable Long studentId) {
        return service.findByStudentId(studentId).stream().map(this::toDTO).toList();
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudentPreferenceDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody StudentPreferenceDTO dto) {
        StudentPreference pref = service.update(id, dto);
        return ResponseEntity.ok(toDTO(pref));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    private StudentPreferenceDTO toDTO(StudentPreference pref) {
        return new StudentPreferenceDTO(
                pref.getId(),
                pref.getStudent().getId(),
                pref.getCourse().getId(),
                pref.getPreferenceRank(),
                pref.getPackName()
        );
    }
}