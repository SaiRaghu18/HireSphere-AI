package com.hirsphere.backend.controller;

import com.hirsphere.backend.dto.JobRecommendationResponse;
import com.hirsphere.backend.dto.UserDTO;
import com.hirsphere.backend.service.JobRecommendationService;
import com.hirsphere.backend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
public class JobRecommendationController {

    private final JobRecommendationService jobRecommendationService;
    private final UserService userService;

    public JobRecommendationController(JobRecommendationService jobRecommendationService, UserService userService) {
        this.jobRecommendationService = jobRecommendationService;
        this.userService = userService;
    }

    @GetMapping("/jobs")
    @PreAuthorize("hasRole('JOB_SEEKER')")
    public ResponseEntity<List<JobRecommendationResponse>> getRecommendedJobs(
            @RequestParam(defaultValue = "5") int limit,
            Principal principal) {
        UserDTO user = userService.getUserByEmail(principal.getName());
        return ResponseEntity.ok(jobRecommendationService.getRecommendedJobs(user.getId(), limit));
    }
}
