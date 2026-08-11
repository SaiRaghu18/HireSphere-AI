package com.hirsphere.backend.dto;

import java.util.List;

public class RecruiterAssistantResponse {

    private Long jobId;
    private String question;
    private String answer;
    private List<CandidateSummaryDTO> candidates;

    public RecruiterAssistantResponse() {}

    public RecruiterAssistantResponse(Long jobId, String question, String answer, List<CandidateSummaryDTO> candidates) {
        this.jobId = jobId;
        this.question = question;
        this.answer = answer;
        this.candidates = candidates;
    }

    public Long getJobId() { return jobId; }
    public void setJobId(Long jobId) { this.jobId = jobId; }

    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }

    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }

    public List<CandidateSummaryDTO> getCandidates() { return candidates; }
    public void setCandidates(List<CandidateSummaryDTO> candidates) { this.candidates = candidates; }

    public static class CandidateSummaryDTO {
        private Long candidateId;
        private String candidateName;
        private Integer matchScore;
        private String applicationStatus;
        private String reason;
        private List<String> matchedSkills;
        private List<String> missingSkills;

        public CandidateSummaryDTO() {}

        public CandidateSummaryDTO(Long candidateId, String candidateName, Integer matchScore, String applicationStatus, String reason, List<String> matchedSkills, List<String> missingSkills) {
            this.candidateId = candidateId;
            this.candidateName = candidateName;
            this.matchScore = matchScore;
            this.applicationStatus = applicationStatus;
            this.reason = reason;
            this.matchedSkills = matchedSkills;
            this.missingSkills = missingSkills;
        }

        public Long getCandidateId() { return candidateId; }
        public void setCandidateId(Long candidateId) { this.candidateId = candidateId; }

        public String getCandidateName() { return candidateName; }
        public void setCandidateName(String candidateName) { this.candidateName = candidateName; }

        public Integer getMatchScore() { return matchScore; }
        public void setMatchScore(Integer matchScore) { this.matchScore = matchScore; }

        public String getApplicationStatus() { return applicationStatus; }
        public void setApplicationStatus(String applicationStatus) { this.applicationStatus = applicationStatus; }

        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }

        public List<String> getMatchedSkills() { return matchedSkills; }
        public void setMatchedSkills(List<String> matchedSkills) { this.matchedSkills = matchedSkills; }

        public List<String> getMissingSkills() { return missingSkills; }
        public void setMissingSkills(List<String> missingSkills) { this.missingSkills = missingSkills; }
    }
}
