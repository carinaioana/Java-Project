package com.example.lab4.controller;

import com.example.lab4.entity.Person;
import com.example.lab4.entity.Role;
import com.example.lab4.repository.PersonRepository;
import com.example.lab4.security.JwtService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final PersonRepository personRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    // authenticates credentials using AuthenticationManager and issues a token via JwtService
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        String token = jwtService.generateToken(userDetails);
        return ResponseEntity.ok(Map.of("token", token));
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody RegisterRequest request) {
        // Simple registration creating a basic User (Person) with STUDENT role
        Person user = new Person(
                request.getName(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                Role.STUDENT
        );
        personRepository.save(user);

        // Auto-login after registration
        String token = jwtService.generateToken(userDetailsService.loadUserByUsername(user.getEmail()));
        return ResponseEntity.ok(Map.of("token", token));
    }

    @Data
    public static class LoginRequest { private String username; private String password; }

    @Data
    public static class RegisterRequest { private String name; private String email; private String password; }
}

//package com.example.lab4.controller;
//
//import lombok.Data;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/login")
//public class AuthController {
//
//    @PostMapping
//    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest request) {
//        // Mock login - in real app, verify credentials
//        Map<String, String> response = new HashMap<>();
//        response.put("message", "Login successful");
//        response.put("username", request.getUsername());
//        response.put("token", "mock-jwt-token-12345");
//
//        return ResponseEntity.ok(response);
//    }
//
//    @Data
//    public static class LoginRequest {
//        private String username;
//        private String password;
//    }
//}