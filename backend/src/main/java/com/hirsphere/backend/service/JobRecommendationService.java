package com.hirsphere.backend.service;

import com.hirsphere.backend.dto.JobRecommendationResponse;

import java.util.List;

public interface JobRecommendationService {
    List<JobRecommendationResponse> getRecommendedJobs(Long candidateId, int limit);
}
