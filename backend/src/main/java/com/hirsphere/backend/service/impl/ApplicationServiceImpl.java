package com.hirsphere.backend.service.impl;

import com.hirsphere.backend.dto.ApplicationDTO;
import com.hirsphere.backend.entity.*;
import com.hirsphere.backend.exception.DuplicateResourceException;
import com.hirsphere.backend.exception.ResourceNotFoundException;
import com.hirsphere.backend.repository.ApplicationRepository;
import com.hirsphere.backend.repository.JobRepository;
import com.hirsphere.backend.repository.UserRepository;
import com.hirsphere.backend.service.ApplicationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final com.hirsphere.backend.service.NotificationService notificationService;

    public ApplicationServiceImpl(ApplicationRepository applicationRepository,
                                  JobRepository jobRepository,
                                  UserRepository userRepository,
                                  com.hirsphere.backend.service.NotificationService notificationService) {
        this.applicationRepository = applicationRepository;
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    @Override
    @Transactional
    public ApplicationDTO apply(Long jobId, Long applicantId, String coverLetter) {
        if (applicationRepository.existsByJobIdAndApplicantId(jobId, applicantId)) {
            throw new DuplicateResourceException("You have already applied for this job");
        }

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));

        if (job.getStatus() != JobStatus.ACTIVE) {
            throw new IllegalArgumentException("This job is no longer accepting applications");
        }

        User applicant = userRepository.findById(applicantId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (applicant.getRole() != UserRole.JOB_SEEKER) {
            throw new IllegalArgumentException("Only candidates/job seekers can apply for jobs");
        }

        Application application = new Application();
        application.setJob(job);
        application.setApplicant(applicant);
        application.setCoverLetter(coverLetter);
        application.setStatus(ApplicationStatus.APPLIED);

        Application saved = applicationRepository.save(application);

        // Send Notification to Candidate
        notificationService.createNotification(
                applicant,
                "Application Submitted",
                "You successfully applied for " + job.getTitle() + ".",
                "APPLICATION_SUBMITTED"
        );

        // Send Notification to Recruiter
        if (job.getPostedBy() != null) {
            notificationService.createNotification(
                    job.getPostedBy(),
                    "New Application",
                    applicant.getName() + " applied for " + job.getTitle() + ".",
                    "NEW_APPLICATION"
            );
        }

        return mapToDTO(saved);
    }

    @Override
    public List<ApplicationDTO> getMyApplications(Long applicantId) {
        return applicationRepository.findByApplicantId(applicantId)
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public List<ApplicationDTO> getApplicationsForJob(Long jobId, Long recruiterId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));

        if (!job.getPostedBy().getId().equals(recruiterId)) {
            throw new IllegalArgumentException("Access denied");
        }

        return applicationRepository.findByJobId(jobId)
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public List<ApplicationDTO> getApplicationsForRecruiter(Long recruiterId) {
        return applicationRepository.findByJobPostedById(recruiterId)
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public List<ApplicationDTO> getAllApplications() {
        return applicationRepository.findAll()
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ApplicationDTO updateStatus(Long applicationId, ApplicationStatus status, Long recruiterId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

        if (!application.getJob().getPostedBy().getId().equals(recruiterId)) {
            throw new IllegalArgumentException("You can only update applications for your own jobs");
        }

        application.setStatus(status);
        Application updated = applicationRepository.save(application);

        // Send Notification to Candidate on status change
        String title = "Application Update";
        String msg = "Your application for " + application.getJob().getTitle() + " has been updated to " + status + ".";
        if (status == ApplicationStatus.SHORTLISTED) {
            title = "Application Shortlisted";
            msg = "Your application for " + application.getJob().getTitle() + " has been shortlisted.";
        } else if (status == ApplicationStatus.INTERVIEW) {
            title = "Interview Scheduled";
            msg = "You have been invited for an interview for " + application.getJob().getTitle() + ".";
        } else if (status == ApplicationStatus.HIRED) {
            title = "Congratulations! Hired";
            msg = "You have been hired for " + application.getJob().getTitle() + "!";
        } else if (status == ApplicationStatus.REJECTED) {
            title = "Application Update";
            msg = "Your application for " + application.getJob().getTitle() + " was not selected at this time.";
        }

        notificationService.createNotification(
                application.getApplicant(),
                title,
                msg,
                "STATUS_CHANGE"
        );

        return mapToDTO(updated);
    }

    @Override
    @Transactional
    public void withdrawApplication(Long applicationId, Long applicantId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

        if (!application.getApplicant().getId().equals(applicantId)) {
            throw new IllegalArgumentException("You can only withdraw your own applications");
        }

        application.setStatus(ApplicationStatus.WITHDRAWN);
        applicationRepository.save(application);
    }

    private ApplicationDTO mapToDTO(Application app) {
        ApplicationDTO dto = new ApplicationDTO();
        dto.setId(app.getId());
        dto.setJobId(app.getJob().getId());
        dto.setJobTitle(app.getJob().getTitle());
        dto.setCompanyName(app.getJob().getCompany().getName());
        dto.setJobLocation(app.getJob().getLocation());
        dto.setApplicantId(app.getApplicant().getId());
        dto.setApplicantName(app.getApplicant().getName());
        dto.setApplicantEmail(app.getApplicant().getEmail());
        if (app.getJob().getPostedBy() != null) {
            dto.setRecruiterName(app.getJob().getPostedBy().getName());
        }
        dto.setStatus(app.getStatus());
        dto.setCoverLetter(app.getCoverLetter());
        dto.setCreatedAt(app.getCreatedAt());
        dto.setUpdatedAt(app.getUpdatedAt());
        return dto;
    }
}
