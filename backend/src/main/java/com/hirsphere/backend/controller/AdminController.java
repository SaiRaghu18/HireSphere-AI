package com.hirsphere.backend.controller;

import com.hirsphere.backend.dto.*;
import com.hirsphere.backend.entity.JobStatus;
import com.hirsphere.backend.service.ApplicationService;
import com.hirsphere.backend.service.CompanyService;
import com.hirsphere.backend.service.JobService;
import com.hirsphere.backend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;
    private final JobService jobService;
    private final CompanyService companyService;
    private final ApplicationService applicationService;

    public AdminController(UserService userService,
                           JobService jobService,
                           CompanyService companyService,
                           ApplicationService applicationService) {
        this.userService = userService;
        this.jobService = jobService;
        this.companyService = companyService;
        this.applicationService = applicationService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardStats> getDashboardStats() {
        return ResponseEntity.ok(userService.getAdminStats());
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getAllUsers(@RequestParam(required = false) String role) {
        if (role != null && !role.trim().isEmpty()) {
            return ResponseEntity.ok(userService.getUsersByRole(role));
        }
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PutMapping("/users/{id}/status")
    public ResponseEntity<UserDTO> updateUserStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String status = body.get("status");
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("Status is required");
        }
        return ResponseEntity.ok(userService.updateUserStatus(id, status));
    }

    @GetMapping("/jobs")
    public ResponseEntity<List<JobDTO>> getAllJobs() {
        return ResponseEntity.ok(jobService.getAllJobs());
    }

    @PutMapping("/jobs/{id}/status")
    public ResponseEntity<JobDTO> updateJobStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String statusStr = body.get("status");
        if (statusStr == null || statusStr.trim().isEmpty()) {
            throw new IllegalArgumentException("Status is required");
        }
        JobStatus status = JobStatus.valueOf(statusStr.toUpperCase());
        return ResponseEntity.ok(jobService.updateJobStatus(id, status));
    }

    @GetMapping("/companies")
    public ResponseEntity<List<CompanyDTO>> getAllCompanies() {
        return ResponseEntity.ok(companyService.getAllCompanies());
    }

    @GetMapping("/applications")
    public ResponseEntity<List<ApplicationDTO>> getAllApplications() {
        return ResponseEntity.ok(applicationService.getAllApplications());
    }
}
