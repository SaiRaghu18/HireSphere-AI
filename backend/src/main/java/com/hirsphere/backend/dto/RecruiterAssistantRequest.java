package com.hirsphere.backend.dto;

import java.util.List;

public class RecruiterAssistantRequest {

    private Long jobId;
    private String question;

    public RecruiterAssistantRequest() {}

    public RecruiterAssistantRequest(Long jobId, String question) {
        this.jobId = jobId;
        this.question = question;
    }

    public Long getJobId() { return jobId; }
    public void setJobId(Long jobId) { this.jobId = jobId; }

    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }
}
