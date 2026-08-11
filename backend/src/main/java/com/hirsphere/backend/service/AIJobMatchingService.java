package com.hirsphere.backend.service;

import com.hirsphere.backend.dto.JobMatchResponse;
import com.hirsphere.backend.entity.Job;

public interface AIJobMatchingService {
    JobMatchResponse matchJobWithCandidate(Job job, String resumeAnalysisJson, Long candidateId);
}
