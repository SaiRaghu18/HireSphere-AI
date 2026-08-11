package com.hirsphere.backend.service;

import com.hirsphere.backend.dto.ApplicationDTO;
import com.hirsphere.backend.entity.ApplicationStatus;

import java.util.List;

public interface ApplicationService {
    ApplicationDTO apply(Long jobId, Long applicantId, String coverLetter);
    List<ApplicationDTO> getMyApplications(Long applicantId);
    List<ApplicationDTO> getApplicationsForJob(Long jobId, Long recruiterId);
    List<ApplicationDTO> getApplicationsForRecruiter(Long recruiterId);
    List<ApplicationDTO> getAllApplications();
    ApplicationDTO updateStatus(Long applicationId, ApplicationStatus status, Long recruiterId);
    void withdrawApplication(Long applicationId, Long applicantId);
}
