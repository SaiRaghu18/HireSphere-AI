package com.hirsphere.backend.controller;

import com.hirsphere.backend.dto.UserDTO;
import com.hirsphere.backend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/recruiters")
public class RecruiterController {

    private final UserService userService;

    public RecruiterController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<UserDTO> getProfile(Principal principal) {
        return ResponseEntity.ok(userService.getUserByEmail(principal.getName()));
    }

    @PutMapping("/profile")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<UserDTO> updateProfile(@RequestBody UserDTO dto, Principal principal) {
        UserDTO user = userService.getUserByEmail(principal.getName());
        return ResponseEntity.ok(userService.updateProfile(user.getId(), dto));
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<com.hirsphere.backend.dto.DashboardStats> getDashboard(Principal principal) {
        UserDTO user = userService.getUserByEmail(principal.getName());
        return ResponseEntity.ok(userService.getRecruiterStats(user.getId()));
    }

    @GetMapping("/candidates/{id}")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<UserDTO> getCandidateProfile(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }
}
