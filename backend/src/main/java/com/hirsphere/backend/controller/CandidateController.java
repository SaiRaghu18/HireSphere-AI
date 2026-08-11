package com.hirsphere.backend.controller;

import com.hirsphere.backend.dto.UserDTO;
import com.hirsphere.backend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/candidates")
public class CandidateController {

    private final UserService userService;

    public CandidateController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    @PreAuthorize("hasRole('JOB_SEEKER')")
    public ResponseEntity<UserDTO> getProfile(Principal principal) {
        return ResponseEntity.ok(userService.getUserByEmail(principal.getName()));
    }

    @PutMapping("/profile")
    @PreAuthorize("hasRole('JOB_SEEKER')")
    public ResponseEntity<UserDTO> updateProfile(@RequestBody UserDTO dto, Principal principal) {
        UserDTO user = userService.getUserByEmail(principal.getName());
        return ResponseEntity.ok(userService.updateProfile(user.getId(), dto));
    }
}
