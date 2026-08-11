package com.hirsphere.backend.service;

import com.hirsphere.backend.dto.JobDTO;
import com.hirsphere.backend.entity.JobStatus;
import com.hirsphere.backend.entity.JobType;

import java.util.List;

public interface JobService {
    JobDTO createJob(JobDTO jobDTO, Long recruiterId);
    JobDTO getJobById(Long id);
    List<JobDTO> getAllActiveJobs();
    List<JobDTO> getAllJobs();
    List<JobDTO> getJobsByRecruiter(Long recruiterId);
    JobDTO updateJob(Long id, JobDTO jobDTO, Long recruiterId);
    JobDTO updateJobStatus(Long id, JobStatus status);
    void deleteJob(Long id, Long recruiterId);
    List<JobDTO> searchJobs(String keyword, String location, JobType jobType);
}
