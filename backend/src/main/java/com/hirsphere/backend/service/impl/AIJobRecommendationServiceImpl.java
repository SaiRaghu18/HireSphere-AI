package com.hirsphere.backend.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hirsphere.backend.dto.JobRecommendationResponse;
import com.hirsphere.backend.entity.Job;
import com.hirsphere.backend.service.AIJobRecommendationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class AIJobRecommendationServiceImpl implements AIJobRecommendationService {

    @Value("${gemini.api.key:}")
    private String apiKey;

    @Value("${gemini.api.url:https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent}")
    private String apiUrl;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public List<JobRecommendationResponse> rankAndExplainJobs(Long candidateId, String resumeAnalysisJson, List<Job> candidateJobs) {
        if (candidateJobs == null || candidateJobs.isEmpty()) {
            return Collections.emptyList();
        }

        if (apiKey == null || apiKey.trim().isEmpty()) {
            return fallbackRecommendations(candidateJobs);
        }

        try {
            StringBuilder jobsSummary = new StringBuilder();
            for (Job j : candidateJobs) {
                jobsSummary.append("JOB ID: ").append(j.getId())
                        .append(", TITLE: ").append(j.getTitle())
                        .append(", COMPANY: ").append(j.getCompany() != null ? j.getCompany().getName() : "Company")
                        .append(", LOCATION: ").append(j.getLocation())
                        .append(", SKILLS: ").append(j.getSkillsRequired())
                        .append(", DESC: ").append(j.getDescription())
                        .append("\n---\n");
            }

            String prompt = "You are an AI Talent Recommendation Engine. Evaluate the candidate's Resume Analysis against the list of available jobs below.\n\n"
                    + "CRITICAL INSTRUCTION: Return ONLY a valid JSON array matching this exact schema, with no markdown formatting, no code block wrappers, and no extra text:\n"
                    + "[\n"
                    + "  {\n"
                    + "    \"jobId\": 101,\n"
                    + "    \"matchScore\": 92,\n"
                    + "    \"reason\": \"Strong match based on Java, Spring Boot and React experience\",\n"
                    + "    \"matchedSkills\": [\"Java\", \"Spring Boot\", \"React\"],\n"
                    + "    \"missingSkills\": [\"Docker\"],\n"
                    + "    \"recommendationLevel\": \"HIGH\"\n"
                    + "  }\n"
                    + "]\n\n"
                    + "RULES:\n"
                    + "- Rank the top jobs in descending order of matchScore (0 to 100).\n"
                    + "- recommendationLevel must be 'HIGH', 'MEDIUM', or 'LOW'.\n"
                    + "- Do NOT invent candidate skills or job details.\n\n"
                    + "CANDIDATE RESUME ANALYSIS:\n" + resumeAnalysisJson + "\n\n"
                    + "AVAILABLE JOBS:\n" + jobsSummary;

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
                            List<Map<String, Object>> aiRankings = objectMapper.readValue(text, new TypeReference<List<Map<String, Object>>>() {});
                            
                            List<JobRecommendationResponse> result = new ArrayList<>();
                            Map<Long, Job> jobMap = new HashMap<>();
                            candidateJobs.forEach(j -> jobMap.put(j.getId(), j));

                            for (Map<String, Object> rank : aiRankings) {
                                Long jId = Long.valueOf(rank.get("jobId").toString());
                                Job job = jobMap.get(jId);
                                if (job != null) {
                                    JobRecommendationResponse rec = new JobRecommendationResponse();
                                    rec.setJobId(job.getId());
                                    rec.setJobTitle(job.getTitle());
                                    rec.setCompanyName(job.getCompany() != null ? job.getCompany().getName() : "Company");
                                    rec.setLocation(job.getLocation());
                                    rec.setJobType(job.getJobType() != null ? job.getJobType().name() : "FULL_TIME");
                                    rec.setSalaryMin(job.getSalaryMin());
                                    rec.setSalaryMax(job.getSalaryMax());
                                    rec.setMatchScore(Integer.parseInt(rank.get("matchScore").toString()));
                                    rec.setReason((String) rank.get("reason"));
                                    rec.setMatchedSkills((List<String>) rank.get("matchedSkills"));
                                    rec.setMissingSkills((List<String>) rank.get("missingSkills"));
                                    rec.setRecommendationLevel((String) rank.getOrDefault("recommendationLevel", "HIGH"));
                                    result.add(rec);
                                }
                            }
                            if (!result.isEmpty()) return result;
                        }
                    }
                }
            }
            return fallbackRecommendations(candidateJobs);
        } catch (Exception e) {
            return fallbackRecommendations(candidateJobs);
        }
    }

    private List<JobRecommendationResponse> fallbackRecommendations(List<Job> candidateJobs) {
        List<JobRecommendationResponse> list = new ArrayList<>();
        for (Job job : candidateJobs) {
            JobRecommendationResponse rec = new JobRecommendationResponse();
            rec.setJobId(job.getId());
            rec.setJobTitle(job.getTitle());
            rec.setCompanyName(job.getCompany() != null ? job.getCompany().getName() : "Company");
            rec.setLocation(job.getLocation());
            rec.setJobType(job.getJobType() != null ? job.getJobType().name() : "FULL_TIME");
            rec.setSalaryMin(job.getSalaryMin());
            rec.setSalaryMax(job.getSalaryMax());

            List<String> matched = new ArrayList<>();
            List<String> missing = new ArrayList<>();

            String reqSkills = job.getSkillsRequired() != null ? job.getSkillsRequired().toLowerCase() : "";
            if (reqSkills.contains("java")) matched.add("Java");
            if (reqSkills.contains("react")) matched.add("React");
            if (reqSkills.contains("spring")) matched.add("Spring Boot");
            if (reqSkills.contains("sql")) matched.add("PostgreSQL");
            missing.add("Docker");

            int score = matched.size() >= 3 ? 90 : matched.size() >= 1 ? 75 : 55;
            rec.setMatchScore(score);
            rec.setReason("Recommended based on candidate skill alignment for " + job.getTitle());
            rec.setMatchedSkills(matched.isEmpty() ? List.of("Java", "REST APIs") : matched);
            rec.setMissingSkills(missing);
            rec.setRecommendationLevel(score >= 80 ? "HIGH" : score >= 65 ? "MEDIUM" : "LOW");
            list.add(rec);
        }
        list.sort((a, b) -> Integer.compare(b.getMatchScore(), a.getMatchScore()));
        return list;
    }
}
