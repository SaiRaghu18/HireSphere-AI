package com.hirsphere.backend.dto;

import com.hirsphere.backend.entity.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationDTO {

    private Long id;
    private Long jobId;
    private String jobTitle;
    private String companyName;
    private String jobLocation;
    private Long applicantId;
    private String applicantName;
    private String applicantEmail;
    private String recruiterName;
    private ApplicationStatus status;
    private String coverLetter;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
