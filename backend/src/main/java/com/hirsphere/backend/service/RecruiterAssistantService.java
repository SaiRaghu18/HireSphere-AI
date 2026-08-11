package com.hirsphere.backend.service;

import com.hirsphere.backend.dto.RecruiterAssistantRequest;
import com.hirsphere.backend.dto.RecruiterAssistantResponse;

public interface RecruiterAssistantService {
    RecruiterAssistantResponse askAssistant(Long recruiterId, RecruiterAssistantRequest request);
}
