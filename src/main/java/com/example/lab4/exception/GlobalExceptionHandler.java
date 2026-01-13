package com.example.lab4.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice // acts as an interceptor that catches exceptions thrown from any controller
public class GlobalExceptionHandler {

    @ExceptionHandler(PreferenceException.class)
    public ResponseEntity<String> handlePreferenceException(PreferenceException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        return ResponseEntity.badRequest().body(errors);
    }
    @org.springframework.web.bind.annotation.ExceptionHandler(RuntimeException.class)
    public org.springframework.http.ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        return org.springframework.http.ResponseEntity.internalServerError().body(ex.getMessage());
    }
}