package com.hirsphere.backend.service;

import com.hirsphere.backend.dto.ResumeAnalysisResponse;

public interface AIResumeAnalyzer {
    ResumeAnalysisResponse analyzeResumeText(String resumeText);
}
