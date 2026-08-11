package com.hirsphere.backend.service.impl;

import com.hirsphere.backend.dto.JobRecommendationResponse;
import com.hirsphere.backend.entity.Application;
import com.hirsphere.backend.entity.ApplicationStatus;
import com.hirsphere.backend.entity.Job;
import com.hirsphere.backend.entity.JobStatus;
import com.hirsphere.backend.entity.ResumeAnalysis;
import com.hirsphere.backend.exception.ResourceNotFoundException;
import com.hirsphere.backend.repository.ApplicationRepository;
import com.hirsphere.backend.repository.JobRepository;
import com.hirsphere.backend.repository.ResumeAnalysisRepository;
import com.hirsphere.backend.service.AIJobRecommendationService;
import com.hirsphere.backend.service.JobRecommendationService;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class JobRecommendationServiceImpl implements JobRecommendationService {

    private final JobRepository jobRepository;
    private final ApplicationRepository applicationRepository;
    private final ResumeAnalysisRepository resumeAnalysisRepository;
    private final AIJobRecommendationService aiJobRecommendationService;

    public JobRecommendationServiceImpl(JobRepository jobRepository,
                                        ApplicationRepository applicationRepository,
                                        ResumeAnalysisRepository resumeAnalysisRepository,
                                        AIJobRecommendationService aiJobRecommendationService) {
        this.jobRepository = jobRepository;
        this.applicationRepository = applicationRepository;
        this.resumeAnalysisRepository = resumeAnalysisRepository;
        this.aiJobRecommendationService = aiJobRecommendationService;
    }

    @Override
    public List<JobRecommendationResponse> getRecommendedJobs(Long candidateId, int limit) {
        // 1. Fetch active jobs from DB
        List<Job> activeJobs = jobRepository.findByStatus(JobStatus.ACTIVE);
        if (activeJobs.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. Application awareness filtering
        List<Application> candidateApps = applicationRepository.findByApplicantId(candidateId);
        Map<Long, ApplicationStatus> appMap = new HashMap<>();
        for (Application app : candidateApps) {
            appMap.put(app.getJob().getId(), app.getStatus());
        }

        // Exclude jobs where candidate was HIRED
        List<Job> eligibleJobs = new ArrayList<>();
        for (Job j : activeJobs) {
            if (appMap.get(j.getId()) != ApplicationStatus.HIRED) {
                eligibleJobs.add(j);
            }
        }

        if (eligibleJobs.isEmpty()) {
            return Collections.emptyList();
        }

        // 3. Limit candidates sent to AI layer to top 15 most relevant
        int candidatePoolSize = Math.min(eligibleJobs.size(), 15);
        List<Job> candidatePool = eligibleJobs.subList(0, candidatePoolSize);

        // 4. Fetch Candidate Resume Analysis
        Optional<ResumeAnalysis> analysisOpt = resumeAnalysisRepository.findByCandidateId(candidateId);
        String resumeJson = analysisOpt.isPresent() ? analysisOpt.get().getJsonResult() : "{}";

        // 5. Run Stage 2 AI ranking and explanation
        List<JobRecommendationResponse> recommendations = aiJobRecommendationService.rankAndExplainJobs(candidateId, resumeJson, candidatePool);

        // 6. Enrich with application status flags & enforce requested result limit
        List<JobRecommendationResponse> finalResults = new ArrayList<>();
        for (JobRecommendationResponse rec : recommendations) {
            if (finalResults.size() >= limit) break;

            ApplicationStatus status = appMap.get(rec.getJobId());
            if (status != null) {
                rec.setAlreadyApplied(true);
                rec.setApplicationStatus(status.name());
            } else {
                rec.setAlreadyApplied(false);
                rec.setApplicationStatus(null);
            }
            finalResults.add(rec);
        }

        return finalResults;
    }
}
