package com.hirsphere.backend.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hirsphere.backend.dto.JobMatchResponse;
import com.hirsphere.backend.entity.Job;
import com.hirsphere.backend.entity.JobMatch;
import com.hirsphere.backend.entity.JobStatus;
import com.hirsphere.backend.entity.ResumeAnalysis;
import com.hirsphere.backend.entity.User;
import com.hirsphere.backend.exception.ResourceNotFoundException;
import com.hirsphere.backend.repository.JobMatchRepository;
import com.hirsphere.backend.repository.JobRepository;
import com.hirsphere.backend.repository.ResumeAnalysisRepository;
import com.hirsphere.backend.repository.UserRepository;
import com.hirsphere.backend.service.AIJobMatchingService;
import com.hirsphere.backend.service.JobMatchService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class JobMatchServiceImpl implements JobMatchService {

    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final ResumeAnalysisRepository resumeAnalysisRepository;
    private final JobMatchRepository jobMatchRepository;
    private final AIJobMatchingService aiJobMatchingService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JobMatchServiceImpl(JobRepository jobRepository,
                               UserRepository userRepository,
                               ResumeAnalysisRepository resumeAnalysisRepository,
                               JobMatchRepository jobMatchRepository,
                               AIJobMatchingService aiJobMatchingService) {
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
        this.resumeAnalysisRepository = resumeAnalysisRepository;
        this.jobMatchRepository = jobMatchRepository;
        this.aiJobMatchingService = aiJobMatchingService;
    }

    @Override
    @Transactional
    public JobMatchResponse matchJob(Long jobId, Long candidateId) {
        User candidate = userRepository.findById(candidateId)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found"));

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));

        if (job.getStatus() == JobStatus.CLOSED) {
            throw new IllegalArgumentException("This job is CLOSED and cannot be matched.");
        }

        ResumeAnalysis resumeAnalysis = resumeAnalysisRepository.findByCandidateId(candidateId)
                .orElseThrow(() -> new ResourceNotFoundException("No resume analysis found for your profile. Please analyze your resume in 'My Profile' before running job matching."));

        // 1. Run AI Job Matching
        JobMatchResponse response = aiJobMatchingService.matchJobWithCandidate(job, resumeAnalysis.getJsonResult(), candidateId);

        // 2. Persist or Update existing JobMatch entity in PostgreSQL
        try {
            String jsonResult = objectMapper.writeValueAsString(response);
            Optional<JobMatch> existing = jobMatchRepository.findByJobIdAndCandidateId(jobId, candidateId);
            JobMatch match = existing.orElseGet(JobMatch::new);
            match.setJob(job);
            match.setCandidate(candidate);
            match.setMatchScore(response.getMatchScore());
            match.setJsonResult(jsonResult);
            jobMatchRepository.save(match);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save job match to database: " + e.getMessage());
        }

        return response;
    }

    @Override
    public JobMatchResponse getJobMatch(Long jobId, Long candidateId) {
        JobMatch match = jobMatchRepository.findByJobIdAndCandidateId(jobId, candidateId)
                .orElseThrow(() -> new ResourceNotFoundException("No existing match found for this job and candidate."));

        try {
            return objectMapper.readValue(match.getJsonResult(), JobMatchResponse.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse stored job match: " + e.getMessage());
        }
    }
}
