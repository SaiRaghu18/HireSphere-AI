package com.hirsphere.backend.controller;

import com.hirsphere.backend.dto.RecruiterAssistantRequest;
import com.hirsphere.backend.dto.RecruiterAssistantResponse;
import com.hirsphere.backend.dto.UserDTO;
import com.hirsphere.backend.service.RecruiterAssistantService;
import com.hirsphere.backend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/recruiter")
public class RecruiterAssistantController {

    private final RecruiterAssistantService recruiterAssistantService;
    private final UserService userService;

    public RecruiterAssistantController(RecruiterAssistantService recruiterAssistantService, UserService userService) {
        this.recruiterAssistantService = recruiterAssistantService;
        this.userService = userService;
    }

    @PostMapping("/assistant")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<RecruiterAssistantResponse> askAssistant(
            @RequestBody RecruiterAssistantRequest request,
            Principal principal) {
        UserDTO user = userService.getUserByEmail(principal.getName());
        return ResponseEntity.ok(recruiterAssistantService.askAssistant(user.getId(), request));
    }
}
