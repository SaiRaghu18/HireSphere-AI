package com.hirsphere.backend.controller;

import com.hirsphere.backend.dto.JobDTO;
import com.hirsphere.backend.entity.JobType;
import com.hirsphere.backend.dto.UserDTO;
import com.hirsphere.backend.service.JobService;
import com.hirsphere.backend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final JobService jobService;
    private final UserService userService;

    public JobController(JobService jobService, UserService userService) {
        this.jobService = jobService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<JobDTO>> getAllActiveJobs() {
        return ResponseEntity.ok(jobService.getAllActiveJobs());
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobDTO> getJobById(@PathVariable Long id) {
        return ResponseEntity.ok(jobService.getJobById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<JobDTO>> searchJobs(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) JobType jobType) {
        return ResponseEntity.ok(jobService.searchJobs(keyword, location, jobType));
    }

    @PostMapping
    public ResponseEntity<JobDTO> createJob(@RequestBody JobDTO jobDTO, Principal principal) {
        UserDTO user = userService.getUserByEmail(principal.getName());
        return ResponseEntity.ok(jobService.createJob(jobDTO, user.getId()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<JobDTO> updateJob(@PathVariable Long id, @RequestBody JobDTO jobDTO, Principal principal) {
        UserDTO user = userService.getUserByEmail(principal.getName());
        return ResponseEntity.ok(jobService.updateJob(id, jobDTO, user.getId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJob(@PathVariable Long id, Principal principal) {
        UserDTO user = userService.getUserByEmail(principal.getName());
        jobService.deleteJob(id, user.getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/recruiter")
    public ResponseEntity<List<JobDTO>> getRecruiterJobs(Principal principal) {
        UserDTO user = userService.getUserByEmail(principal.getName());
        return ResponseEntity.ok(jobService.getJobsByRecruiter(user.getId()));
    }
}
