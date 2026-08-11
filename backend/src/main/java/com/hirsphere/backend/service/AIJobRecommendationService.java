package com.hirsphere.backend.service;

import com.hirsphere.backend.dto.JobRecommendationResponse;
import com.hirsphere.backend.entity.Job;

import java.util.List;

public interface AIJobRecommendationService {
    List<JobRecommendationResponse> rankAndExplainJobs(Long candidateId, String resumeAnalysisJson, List<Job> candidateJobs);
}
