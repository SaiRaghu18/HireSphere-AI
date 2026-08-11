package com.hirsphere.backend.controller;

import com.hirsphere.backend.dto.ApplicationDTO;
import com.hirsphere.backend.entity.ApplicationStatus;
import com.hirsphere.backend.dto.UserDTO;
import com.hirsphere.backend.service.ApplicationService;
import com.hirsphere.backend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    private final ApplicationService applicationService;
    private final UserService userService;

    public ApplicationController(ApplicationService applicationService, UserService userService) {
        this.applicationService = applicationService;
        this.userService = userService;
    }

    @PostMapping({"/apply/{jobId}", "/job/{jobId}"})
    public ResponseEntity<ApplicationDTO> apply(
            @PathVariable Long jobId,
            @RequestParam(required = false) String coverLetter,
            Principal principal) {
        UserDTO user = userService.getUserByEmail(principal.getName());
        return ResponseEntity.ok(applicationService.apply(jobId, user.getId(), coverLetter));
    }

    @GetMapping("/my")
    public ResponseEntity<List<ApplicationDTO>> getMyApplications(Principal principal) {
        UserDTO user = userService.getUserByEmail(principal.getName());
        return ResponseEntity.ok(applicationService.getMyApplications(user.getId()));
    }

    @GetMapping("/job/{jobId}")
    public ResponseEntity<List<ApplicationDTO>> getApplicationsForJob(@PathVariable Long jobId, Principal principal) {
        UserDTO user = userService.getUserByEmail(principal.getName());
        return ResponseEntity.ok(applicationService.getApplicationsForJob(jobId, user.getId()));
    }

    @GetMapping("/recruiter")
    public ResponseEntity<List<ApplicationDTO>> getApplicationsForRecruiter(Principal principal) {
        UserDTO user = userService.getUserByEmail(principal.getName());
        return ResponseEntity.ok(applicationService.getApplicationsForRecruiter(user.getId()));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApplicationDTO> updateStatus(
            @PathVariable Long id,
            @RequestBody(required = false) com.hirsphere.backend.dto.ApplicationStatusUpdateDTO body,
            @RequestParam(required = false) ApplicationStatus status,
            Principal principal) {
        UserDTO user = userService.getUserByEmail(principal.getName());
        ApplicationStatus newStatus = (body != null && body.getStatus() != null) ? body.getStatus() : status;
        if (newStatus == null) {
            throw new IllegalArgumentException("Status must be provided in request body or request param");
        }
        return ResponseEntity.ok(applicationService.updateStatus(id, newStatus, user.getId()));
    }

    @DeleteMapping("/{id}/withdraw")
    public ResponseEntity<Void> withdrawApplication(@PathVariable Long id, Principal principal) {
        UserDTO user = userService.getUserByEmail(principal.getName());
        applicationService.withdrawApplication(id, user.getId());
        return ResponseEntity.noContent().build();
    }
}
