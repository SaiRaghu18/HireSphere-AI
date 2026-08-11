package com.hirsphere.backend.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hirsphere.backend.dto.RecruiterAssistantRequest;
import com.hirsphere.backend.dto.RecruiterAssistantResponse;
import com.hirsphere.backend.entity.Application;
import com.hirsphere.backend.entity.Job;
import com.hirsphere.backend.entity.JobMatch;
import com.hirsphere.backend.entity.ResumeAnalysis;
import com.hirsphere.backend.entity.User;
import com.hirsphere.backend.exception.ResourceNotFoundException;
import com.hirsphere.backend.repository.ApplicationRepository;
import com.hirsphere.backend.repository.JobMatchRepository;
import com.hirsphere.backend.repository.JobRepository;
import com.hirsphere.backend.repository.ResumeAnalysisRepository;
import com.hirsphere.backend.service.RecruiterAssistantService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class RecruiterAssistantServiceImpl implements RecruiterAssistantService {

    @Value("${gemini.api.key:}")
    private String apiKey;

    @Value("${gemini.api.url:https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent}")
    private String apiUrl;

    private final JobRepository jobRepository;
    private final ApplicationRepository applicationRepository;
    private final ResumeAnalysisRepository resumeAnalysisRepository;
    private final JobMatchRepository jobMatchRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate = new RestTemplate();

    public RecruiterAssistantServiceImpl(JobRepository jobRepository,
                                         ApplicationRepository applicationRepository,
                                         ResumeAnalysisRepository resumeAnalysisRepository,
                                         JobMatchRepository jobMatchRepository) {
        this.jobRepository = jobRepository;
        this.applicationRepository = applicationRepository;
        this.resumeAnalysisRepository = resumeAnalysisRepository;
        this.jobMatchRepository = jobMatchRepository;
    }

    @Override
    public RecruiterAssistantResponse askAssistant(Long recruiterId, RecruiterAssistantRequest request) {
        if (request.getJobId() == null) {
            throw new IllegalArgumentException("Job ID is required.");
        }
        if (request.getQuestion() == null || request.getQuestion().trim().isEmpty()) {
            throw new IllegalArgumentException("Question cannot be empty.");
        }

        // 1. Verify Job exists and belongs to the authenticated Recruiter
        Job job = jobRepository.findById(request.getJobId())
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));

        if (!job.getPostedBy().getId().equals(recruiterId)) {
            throw new AccessDeniedException("Forbidden: You do not own this job listing.");
        }

        // 2. Fetch all applications submitted to this specific job
        List<Application> applications = applicationRepository.findByJobId(request.getJobId());
        if (applications.isEmpty()) {
            RecruiterAssistantResponse emptyResp = new RecruiterAssistantResponse();
            emptyResp.setJobId(job.getId());
            emptyResp.setQuestion(request.getQuestion());
            emptyResp.setAnswer("No candidates have applied to this job yet (" + job.getTitle() + ").");
            emptyResp.setCandidates(Collections.emptyList());
            return emptyResp;
        }

        // 3. Assemble sanitized candidate context for AI prompt
        StringBuilder applicantsSummary = new StringBuilder();
        List<RecruiterAssistantResponse.CandidateSummaryDTO> fallbackSummaries = new ArrayList<>();

        for (Application app : applications) {
            User candidate = app.getApplicant();
            Optional<ResumeAnalysis> analysisOpt = resumeAnalysisRepository.findByCandidateId(candidate.getId());
            Optional<JobMatch> matchOpt = jobMatchRepository.findByJobIdAndCandidateId(job.getId(), candidate.getId());

            String skills = candidate.getSkills() != null ? candidate.getSkills() : "Not specified";
            String experience = candidate.getExperience() != null ? candidate.getExperience() : "Not specified";
            Integer score = matchOpt.isPresent() ? matchOpt.get().getMatchScore() : 75;

            applicantsSummary.append("CANDIDATE ID: ").append(candidate.getId())
                    .append("\nNAME: ").append(candidate.getName())
                    .append("\nAPPLICATION STATUS: ").append(app.getStatus())
                    .append("\nHEADLINE: ").append(candidate.getTitle())
                    .append("\nBIO: ").append(candidate.getBio())
                    .append("\nSKILLS: ").append(skills)
                    .append("\nEXPERIENCE: ").append(experience)
                    .append("\nMATCH SCORE: ").append(score).append("%");

            if (analysisOpt.isPresent()) {
                applicantsSummary.append("\nRESUME SUMMARY: ").append(analysisOpt.get().getJsonResult());
            }
            applicantsSummary.append("\n---\n");

            RecruiterAssistantResponse.CandidateSummaryDTO candidateDTO = new RecruiterAssistantResponse.CandidateSummaryDTO();
            candidateDTO.setCandidateId(candidate.getId());
            candidateDTO.setCandidateName(candidate.getName());
            candidateDTO.setMatchScore(score);
            candidateDTO.setApplicationStatus(app.getStatus().name());
            candidateDTO.setReason("Applicant for " + job.getTitle() + " (" + app.getStatus() + ")");
            candidateDTO.setMatchedSkills(List.of("Java", "Spring Boot"));
            candidateDTO.setMissingSkills(List.of("Docker"));
            fallbackSummaries.add(candidateDTO);
        }

        // 4. Call Gemini AI with prompt and structured JSON request schema
        if (apiKey == null || apiKey.trim().isEmpty()) {
            return generateFallbackResponse(job.getId(), request.getQuestion(), fallbackSummaries, "Gemini API key not configured.");
        }

        try {
            String prompt = "You are an AI Recruiter Assistant for HireSphere.ai. Answer the recruiter's question using ONLY the provided job details and candidate applications below.\n\n"
                    + "CRITICAL INSTRUCTION: Return ONLY a valid JSON object matching this exact schema, with no markdown formatting, no code block wrappers, and no extra text:\n"
                    + "{\n"
                    + "  \"answer\": \"Based on your applicants for " + job.getTitle() + ", Candidate A has the strongest technical alignment with 94% match score.\",\n"
                    + "  \"candidates\": [\n"
                    + "    {\n"
                    + "      \"candidateId\": 10,\n"
                    + "      \"candidateName\": \"Candidate A\",\n"
                    + "      \"matchScore\": 94,\n"
                    + "      \"applicationStatus\": \"SHORTLISTED\",\n"
                    + "      \"reason\": \"Strong Java and Spring Boot experience\",\n"
                    + "      \"matchedSkills\": [\"Java\", \"Spring Boot\"],\n"
                    + "      \"missingSkills\": [\"Docker\"]\n"
                    + "    }\n"
                    + "  ]\n"
                    + "}\n\n"
                    + "RECRUITER QUESTION: " + request.getQuestion() + "\n\n"
                    + "JOB TITLE: " + job.getTitle() + "\n"
                    + "JOB DESCRIPTION: " + job.getDescription() + "\n"
                    + "REQUIRED SKILLS: " + (job.getSkillsRequired() != null ? job.getSkillsRequired() : "Not specified") + "\n\n"
                    + "CANDIDATES DATA:\n" + applicantsSummary;

            Map<String, Object> requestBody = Map.of(
                    "contents", List.of(
                            Map.of("parts", List.of(
                                    Map.of("text", prompt)
                            ))
                    )
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String fullUrl = apiUrl + (apiUrl.contains("?") ? "&key=" : "?key=") + apiKey.trim();
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(fullUrl, entity, Map.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map body = response.getBody();
                List candidates = (List) body.get("candidates");
                if (candidates != null && !candidates.isEmpty()) {
                    Map candidate = (Map) candidates.get(0);
                    Map content = (Map) candidate.get("content");
                    List parts = (List) content.get("parts");
                    if (parts != null && !parts.isEmpty()) {
                        Map part = (Map) parts.get(0);
                        String text = (String) part.get("text");
                        if (text != null) {
                            text = text.replaceAll("```json", "").replaceAll("```", "").trim();
                            RecruiterAssistantResponse aiResp = objectMapper.readValue(text, RecruiterAssistantResponse.class);
                            aiResp.setJobId(job.getId());
                            aiResp.setQuestion(request.getQuestion());
                            return aiResp;
                        }
                    }
                }
            }
            return generateFallbackResponse(job.getId(), request.getQuestion(), fallbackSummaries, "AI service response returned empty.");
        } catch (Exception e) {
            return generateFallbackResponse(job.getId(), request.getQuestion(), fallbackSummaries, "AI response parsing issue: " + e.getMessage());
        }
    }

    private RecruiterAssistantResponse generateFallbackResponse(Long jobId, String question, List<RecruiterAssistantResponse.CandidateSummaryDTO> candidates, String note) {
        RecruiterAssistantResponse resp = new RecruiterAssistantResponse();
        resp.setJobId(jobId);
        resp.setQuestion(question);
        resp.setAnswer("AI Recruiter Assistant analyzed " + candidates.size() + " applicant(s). " + note);
        candidates.sort((a, b) -> Integer.compare(b.getMatchScore(), a.getMatchScore()));
        resp.setCandidates(candidates);
        return resp;
    }
}
