package com.example.lab4.dto;

import java.util.List;
import java.util.Map;

public class StableMatchDTOs {

    // Request Objects
    public record MatchRequest(List<StudentReq> students, List<CourseReq> courses) {}
    public record StudentReq(String id, List<String> preferences) {}
    public record CourseReq(String id, int capacity, List<String> studentRankings) {}

    // Response Object
    public record MatchResponse(Map<String, String> assignments, List<String> unassigned) {}
}