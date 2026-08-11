package com.hirsphere.backend.repository;

import com.hirsphere.backend.entity.ResumeAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ResumeAnalysisRepository extends JpaRepository<ResumeAnalysis, Long> {

    Optional<ResumeAnalysis> findByResumeId(Long resumeId);

    Optional<ResumeAnalysis> findByCandidateId(Long candidateId);
}
