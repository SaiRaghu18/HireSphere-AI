package com.hirsphere.backend.controller;

import com.hirsphere.backend.dto.ResumeAnalysisResponse;
import com.hirsphere.backend.dto.UserDTO;
import com.hirsphere.backend.service.ResumeAnalysisService;
import com.hirsphere.backend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/resumes")
public class ResumeAnalysisController {

    private final ResumeAnalysisService resumeAnalysisService;
    private final UserService userService;

    public ResumeAnalysisController(ResumeAnalysisService resumeAnalysisService, UserService userService) {
        this.resumeAnalysisService = resumeAnalysisService;
        this.userService = userService;
    }

    @PostMapping("/{resumeId}/analyze")
    @PreAuthorize("hasRole('JOB_SEEKER')")
    public ResponseEntity<ResumeAnalysisResponse> analyzeResume(@PathVariable Long resumeId, Principal principal) {
        UserDTO user = userService.getUserByEmail(principal.getName());
        return ResponseEntity.ok(resumeAnalysisService.analyzeResume(resumeId, user.getId()));
    }

    @GetMapping("/{resumeId}/analysis")
    @PreAuthorize("hasRole('JOB_SEEKER')")
    public ResponseEntity<ResumeAnalysisResponse> getResumeAnalysis(@PathVariable Long resumeId, Principal principal) {
        UserDTO user = userService.getUserByEmail(principal.getName());
        return ResponseEntity.ok(resumeAnalysisService.getResumeAnalysis(resumeId, user.getId()));
    }
}
