package com.hirsphere.backend.service;

import com.hirsphere.backend.dto.JobMatchResponse;

public interface JobMatchService {
    JobMatchResponse matchJob(Long jobId, Long candidateId);
    JobMatchResponse getJobMatch(Long jobId, Long candidateId);
}
