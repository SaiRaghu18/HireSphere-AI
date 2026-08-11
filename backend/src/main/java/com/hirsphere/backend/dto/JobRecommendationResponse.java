package com.hirsphere.backend.dto;

import java.util.List;

public class JobRecommendationResponse {

    private Long jobId;
    private String jobTitle;
    private String companyName;
    private String location;
    private String jobType;
    private Double salaryMin;
    private Double salaryMax;
    private Integer matchScore;
    private String reason;
    private List<String> matchedSkills;
    private List<String> missingSkills;
    private String recommendationLevel; // "HIGH", "MEDIUM", "LOW"
    private Boolean alreadyApplied;
    private String applicationStatus;

    public JobRecommendationResponse() {}

    public JobRecommendationResponse(Long jobId, String jobTitle, String companyName, String location, String jobType, Double salaryMin, Double salaryMax, Integer matchScore, String reason, List<String> matchedSkills, List<String> missingSkills, String recommendationLevel, Boolean alreadyApplied, String applicationStatus) {
        this.jobId = jobId;
        this.jobTitle = jobTitle;
        this.companyName = companyName;
        this.location = location;
        this.jobType = jobType;
        this.salaryMin = salaryMin;
        this.salaryMax = salaryMax;
        this.matchScore = matchScore;
        this.reason = reason;
        this.matchedSkills = matchedSkills;
        this.missingSkills = missingSkills;
        this.recommendationLevel = recommendationLevel;
        this.alreadyApplied = alreadyApplied;
        this.applicationStatus = applicationStatus;
    }

    public Long getJobId() { return jobId; }
    public void setJobId(Long jobId) { this.jobId = jobId; }

    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getJobType() { return jobType; }
    public void setJobType(String jobType) { this.jobType = jobType; }

    public Double getSalaryMin() { return salaryMin; }
    public void setSalaryMin(Double salaryMin) { this.salaryMin = salaryMin; }

    public Double getSalaryMax() { return salaryMax; }
    public void setSalaryMax(Double salaryMax) { this.salaryMax = salaryMax; }

    public Integer getMatchScore() { return matchScore; }
    public void setMatchScore(Integer matchScore) {
        if (matchScore != null) {
            this.matchScore = Math.max(0, Math.min(100, matchScore));
        } else {
            this.matchScore = 0;
        }
    }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public List<String> getMatchedSkills() { return matchedSkills; }
    public void setMatchedSkills(List<String> matchedSkills) { this.matchedSkills = matchedSkills; }

    public List<String> getMissingSkills() { return missingSkills; }
    public void setMissingSkills(List<String> missingSkills) { this.missingSkills = missingSkills; }

    public String getRecommendationLevel() { return recommendationLevel; }
    public void setRecommendationLevel(String recommendationLevel) { this.recommendationLevel = recommendationLevel; }

    public Boolean getAlreadyApplied() { return alreadyApplied; }
    public void setAlreadyApplied(Boolean alreadyApplied) { this.alreadyApplied = alreadyApplied; }

    public String getApplicationStatus() { return applicationStatus; }
    public void setApplicationStatus(String applicationStatus) { this.applicationStatus = applicationStatus; }
}
