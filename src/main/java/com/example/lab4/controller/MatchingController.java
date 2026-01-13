package com.example.lab4.controller;

import com.example.lab4.service.MatchingIntegrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/matching")
@RequiredArgsConstructor
public class MatchingController {

    private final MatchingIntegrationService matchingService;

    @PostMapping("/run")
    public ResponseEntity<String> runMatching() {
        matchingService.runMatchingForAllPacks();
        return ResponseEntity.ok("Matching process initiated for all packs.");
    }
}