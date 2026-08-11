package com.hirsphere.backend.dto;

import java.util.List;

public class JobMatchResponse {

    private Long jobId;
    private Long candidateId;
    private Integer matchScore;
    private List<String> matchedSkills;
    private List<String> missingSkills;
    private String matchingExperience;
    private String educationMatch;
    private List<String> strengths;
    private List<String> gaps;
    private String recommendation;

    public JobMatchResponse() {}

    public JobMatchResponse(Long jobId, Long candidateId, Integer matchScore, List<String> matchedSkills, List<String> missingSkills, String matchingExperience, String educationMatch, List<String> strengths, List<String> gaps, String recommendation) {
        this.jobId = jobId;
        this.candidateId = candidateId;
        this.matchScore = matchScore;
        this.matchedSkills = matchedSkills;
        this.missingSkills = missingSkills;
        this.matchingExperience = matchingExperience;
        this.educationMatch = educationMatch;
        this.strengths = strengths;
        this.gaps = gaps;
        this.recommendation = recommendation;
    }

    public Long getJobId() { return jobId; }
    public void setJobId(Long jobId) { this.jobId = jobId; }

    public Long getCandidateId() { return candidateId; }
    public void setCandidateId(Long candidateId) { this.candidateId = candidateId; }

    public Integer getMatchScore() { return matchScore; }
    public void setMatchScore(Integer matchScore) {
        if (matchScore != null) {
            this.matchScore = Math.max(0, Math.min(100, matchScore));
        } else {
            this.matchScore = 0;
        }
    }

    public List<String> getMatchedSkills() { return matchedSkills; }
    public void setMatchedSkills(List<String> matchedSkills) { this.matchedSkills = matchedSkills; }

    public List<String> getMissingSkills() { return missingSkills; }
    public void setMissingSkills(List<String> missingSkills) { this.missingSkills = missingSkills; }

    public String getMatchingExperience() { return matchingExperience; }
    public void setMatchingExperience(String matchingExperience) { this.matchingExperience = matchingExperience; }

    public String getEducationMatch() { return educationMatch; }
    public void setEducationMatch(String educationMatch) { this.educationMatch = educationMatch; }

    public List<String> getStrengths() { return strengths; }
    public void setStrengths(List<String> strengths) { this.strengths = strengths; }

    public List<String> getGaps() { return gaps; }
    public void setGaps(List<String> gaps) { this.gaps = gaps; }

    public String getRecommendation() { return recommendation; }
    public void setRecommendation(String recommendation) { this.recommendation = recommendation; }
}
