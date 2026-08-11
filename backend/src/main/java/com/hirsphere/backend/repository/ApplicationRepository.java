package com.hirsphere.backend.repository;

import com.hirsphere.backend.entity.Application;
import com.hirsphere.backend.entity.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    List<Application> findByApplicantId(Long applicantId);

    List<Application> findByJobId(Long jobId);

    List<Application> findByJobIdAndStatus(Long jobId, ApplicationStatus status);

    List<Application> findByJobPostedById(Long recruiterId);

    Optional<Application> findByJobIdAndApplicantId(Long jobId, Long applicantId);

    boolean existsByJobIdAndApplicantId(Long jobId, Long applicantId);

    long countByApplicantId(Long applicantId);

    long countByApplicantIdAndStatus(Long applicantId, ApplicationStatus status);

    long countByJobPostedById(Long recruiterId);

    long countByJobPostedByIdAndStatus(Long recruiterId, ApplicationStatus status);

    long countByStatus(ApplicationStatus status);
}
