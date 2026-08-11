package com.hirsphere.backend.controller;

import com.hirsphere.backend.dto.JobMatchResponse;
import com.hirsphere.backend.dto.UserDTO;
import com.hirsphere.backend.service.JobMatchService;
import com.hirsphere.backend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/jobs")
public class JobMatchController {

    private final JobMatchService jobMatchService;
    private final UserService userService;

    public JobMatchController(JobMatchService jobMatchService, UserService userService) {
        this.jobMatchService = jobMatchService;
        this.userService = userService;
    }

    @PostMapping("/{jobId}/match")
    @PreAuthorize("hasRole('JOB_SEEKER')")
    public ResponseEntity<JobMatchResponse> matchJob(@PathVariable Long jobId, Principal principal) {
        UserDTO user = userService.getUserByEmail(principal.getName());
        return ResponseEntity.ok(jobMatchService.matchJob(jobId, user.getId()));
    }

    @GetMapping("/{jobId}/match")
    @PreAuthorize("hasRole('JOB_SEEKER')")
    public ResponseEntity<JobMatchResponse> getJobMatch(@PathVariable Long jobId, Principal principal) {
        UserDTO user = userService.getUserByEmail(principal.getName());
        return ResponseEntity.ok(jobMatchService.getJobMatch(jobId, user.getId()));
    }
}
