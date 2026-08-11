package com.hirsphere.backend.controller;

import com.hirsphere.backend.dto.DashboardStats;
import com.hirsphere.backend.dto.UserDTO;
import com.hirsphere.backend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public ResponseEntity<UserDTO> getProfile(Principal principal) {
        return ResponseEntity.ok(userService.getUserByEmail(principal.getName()));
    }

    @PutMapping("/profile")
    public ResponseEntity<UserDTO> updateProfile(@RequestBody UserDTO dto, Principal principal) {
        UserDTO user = userService.getUserByEmail(principal.getName());
        return ResponseEntity.ok(userService.updateProfile(user.getId(), dto));
    }

    @GetMapping("/stats")
    public ResponseEntity<DashboardStats> getStats(Principal principal) {
        UserDTO user = userService.getUserByEmail(principal.getName());
        if (user.getRole().name().equals("ADMIN")) {
            return ResponseEntity.ok(userService.getAdminStats());
        } else if (user.getRole().name().equals("RECRUITER")) {
            return ResponseEntity.ok(userService.getRecruiterStats(user.getId()));
        } else {
            return ResponseEntity.ok(userService.getJobSeekerStats(user.getId()));
        }
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
}
