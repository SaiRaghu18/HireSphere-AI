package com.hirsphere.backend.repository;

import com.hirsphere.backend.entity.JobMatch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JobMatchRepository extends JpaRepository<JobMatch, Long> {

    Optional<JobMatch> findByJobIdAndCandidateId(Long jobId, Long candidateId);
}
