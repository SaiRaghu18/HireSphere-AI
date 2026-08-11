package com.hirsphere.backend.service;

import com.hirsphere.backend.dto.ResumeAnalysisResponse;

public interface ResumeAnalysisService {
    ResumeAnalysisResponse analyzeResume(Long resumeId, Long candidateId);
    ResumeAnalysisResponse getResumeAnalysis(Long resumeId, Long candidateId);
}
