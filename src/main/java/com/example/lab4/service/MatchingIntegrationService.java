package com.example.lab4.service;

import com.example.lab4.dto.StableMatchDTOs.*;
import com.example.lab4.entity.*;
import com.example.lab4.repository.CourseRepository;
import com.example.lab4.repository.GradeRepository;
import com.example.lab4.repository.InstructorPreferenceRepository;
import com.example.lab4.repository.PackRepository;
import com.example.lab4.repository.StudentPreferenceRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class MatchingIntegrationService {

    private final PackRepository packRepository;
    private final CourseRepository courseRepository;
    private final StudentPreferenceRepository preferenceRepository;
    private final GradeRepository gradeRepository;
    private final InstructorPreferenceRepository instructorPreferenceRepository;

    private final RestClient restClient;

    // USE THE SERVICE NAME REGISTERED IN EUREKA (Not localhost)
    private static final String STABLE_MATCH_URL = "http://StableMatch/api/match/solve";

    // Inject the LoadBalanced Builder we defined in WebConfig
    public MatchingIntegrationService(PackRepository packRepository,
                                      CourseRepository courseRepository,
                                      StudentPreferenceRepository preferenceRepository,
                                      GradeRepository gradeRepository,
                                      InstructorPreferenceRepository instructorPreferenceRepository,
                                      RestClient.Builder restClientBuilder) {
        this.packRepository = packRepository;
        this.courseRepository = courseRepository;
        this.preferenceRepository = preferenceRepository;
        this.gradeRepository = gradeRepository;
        this.instructorPreferenceRepository = instructorPreferenceRepository;
        this.restClient = restClientBuilder.build();
    }

    public void runMatchingForAllPacks() {
        List<Pack> packs = packRepository.findAll();
        for (Pack pack : packs) {
            log.info("Starting matching process for pack: {}", pack.getName());

            MatchRequest request = prepareRequestForPack(pack);

            if (request.students().isEmpty() || request.courses().isEmpty()) {
                log.warn("Skipping pack {} (insufficient data)", pack.getName());
                continue;
            }

            CompletableFuture<MatchResponse> futureResponse = callStableMatchService(request);

            try {
                MatchResponse response = futureResponse.join();
                if (response != null) {
                    log.info("Matching completed for {}. Assigned: {}, Unassigned: {}",
                            pack.getName(), response.assignments().size(), response.unassigned().size());
                }
            } catch (Exception e) {
                log.error("Error during matching execution: {}", e.getMessage());
            }
        }
    }

    private MatchRequest prepareRequestForPack(Pack pack) {
        // --- Step A: Prepare Students ---
        List<StudentPreference> allPrefs = preferenceRepository.findByPackName(pack.getName());

        Map<String, List<String>> studentMap = new HashMap<>();
        for (StudentPreference pref : allPrefs) {
            String studentCode = pref.getStudent().getCode();
            String courseAbbr = pref.getCourse().getAbbr();
            studentMap.computeIfAbsent(studentCode, k -> new ArrayList<>()).add(courseAbbr);
        }

        List<StudentReq> studentReqs = studentMap.entrySet().stream()
                .map(entry -> new StudentReq(entry.getKey(), entry.getValue()))
                .toList();

        // --- Step B: Prepare Courses with RANKINGS ---
        List<Course> optionalCourses = pack.getCourses().stream()
                .filter(c -> c.getType() == CourseType.OPTIONAL)
                .toList();

        List<CourseReq> courseReqs = new ArrayList<>();

        for (Course course : optionalCourses) {
            List<InstructorPreference> weights = instructorPreferenceRepository.findByOptionalCourse(course);

            List<String> interestedStudentCodes = studentMap.entrySet().stream()
                    .filter(entry -> entry.getValue().contains(course.getAbbr()))
                    .map(Map.Entry::getKey)
                    .toList();

            Map<String, Double> studentScores = new HashMap<>();
            for (String studentCode : interestedStudentCodes) {
                double score = calculateStudentScore(studentCode, weights);
                studentScores.put(studentCode, score);
            }

            List<String> rankedStudents = interestedStudentCodes.stream()
                    .sorted((s1, s2) -> Double.compare(studentScores.get(s2), studentScores.get(s1)))
                    .toList();

            int capacity = course.getGroupCount() != null ? course.getGroupCount() : 30;
            courseReqs.add(new CourseReq(course.getAbbr(), capacity, rankedStudents));
        }

        return new MatchRequest(studentReqs, courseReqs);
    }

    private double calculateStudentScore(String studentCode, List<InstructorPreference> weights) {
        if (weights.isEmpty()) return 0.0;

        List<Grade> grades = gradeRepository.findByStudentCode(studentCode);
        double totalWeightedScore = 0.0;
        double totalWeight = 0.0;

        for (InstructorPreference rule : weights) {
            String targetAbbr = rule.getCompulsoryCourse().getAbbr();
            Double ruleWeight = rule.getWeight();

            double gradeValue = grades.stream()
                    .filter(g -> g.getCourse().getAbbr().equals(targetAbbr))
                    .mapToDouble(Grade::getValue)
                    .findFirst()
                    .orElse(0.0);

            totalWeightedScore += gradeValue * ruleWeight;
            totalWeight += ruleWeight;
        }

        return (totalWeight == 0) ? 0 : (totalWeightedScore / totalWeight);
    }

    // --- RESILIENCE PATTERNS ---

    @Retry(name = "stableMatchService", fallbackMethod = "fallbackMatching")
    @CircuitBreaker(name = "stableMatchService", fallbackMethod = "fallbackMatching")
    @TimeLimiter(name = "stableMatchService", fallbackMethod = "fallbackMatching")
    public CompletableFuture<MatchResponse> callStableMatchService(MatchRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            log.info("Calling StableMatch Service via LoadBalancer...");
            return restClient.post()
                    .uri(STABLE_MATCH_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(MatchResponse.class);
        });
    }

    public CompletableFuture<MatchResponse> fallbackMatching(MatchRequest request, Throwable t) {
        log.error("StableMatch service failed: {}. Executing Fallback.", t.getMessage());
        MatchResponse safeResponse = new MatchResponse(Collections.emptyMap(),
                request.students().stream().map(StudentReq::id).toList());
        return CompletableFuture.completedFuture(safeResponse);
    }
}