package com.hirsphere.backend.controller;

import com.hirsphere.backend.entity.Resume;
import com.hirsphere.backend.dto.UserDTO;
import com.hirsphere.backend.service.ResumeService;
import com.hirsphere.backend.service.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

@RestController
@RequestMapping("/api/resumes")
public class ResumeController {

    private final ResumeService resumeService;
    private final UserService userService;

    public ResumeController(ResumeService resumeService, UserService userService) {
        this.resumeService = resumeService;
        this.userService = userService;
    }

    @PostMapping("/upload")
    @PreAuthorize("hasRole('JOB_SEEKER')")
    public ResponseEntity<Resume> uploadResume(@RequestParam("file") MultipartFile file, Principal principal) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
        }
        if (!"application/pdf".equalsIgnoreCase(file.getContentType())) {
            throw new IllegalArgumentException("Only PDF files are allowed");
        }
        if (file.getSize() > 10 * 1024 * 1024) { // 10MB
            throw new IllegalArgumentException("File size exceeds the 10MB limit");
        }

        UserDTO user = userService.getUserByEmail(principal.getName());
        return ResponseEntity.ok(resumeService.uploadResume(file, user.getId()));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('JOB_SEEKER')")
    public ResponseEntity<Resume> getMyResume(Principal principal) {
        UserDTO user = userService.getUserByEmail(principal.getName());
        try {
            return ResponseEntity.ok(resumeService.getResumeByUserId(user.getId()));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Resume> getResumeById(@PathVariable Long id, Principal principal) {
        UserDTO user = userService.getUserByEmail(principal.getName());
        Resume resume = resumeService.getResumeById(id);
        
        // Authenticated candidate can only view their own resume
        if (user.getRole().name().equals("JOB_SEEKER") && !resume.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        return ResponseEntity.ok(resume);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('JOB_SEEKER')")
    public ResponseEntity<Void> deleteResume(@PathVariable Long id, Principal principal) {
        UserDTO user = userService.getUserByEmail(principal.getName());
        resumeService.deleteResume(id, user.getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/download/{userId}")
    public ResponseEntity<byte[]> downloadResume(@PathVariable Long userId, Principal principal) {
        UserDTO currentUser = userService.getUserByEmail(principal.getName());
        
        // Candidates can only download their own resumes
        if (currentUser.getRole().name().equals("JOB_SEEKER") && !currentUser.getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        byte[] data = resumeService.downloadResume(userId);
        Resume resume = resumeService.getResumeByUserId(userId);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(resume.getFileType() != null ? resume.getFileType() : "application/pdf"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resume.getFileName() + "\"")
                .body(data);
    }
}
