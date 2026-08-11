package com.hirsphere.backend.service.impl;

import com.hirsphere.backend.dto.JobDTO;
import com.hirsphere.backend.entity.*;
import com.hirsphere.backend.exception.ResourceNotFoundException;
import com.hirsphere.backend.repository.ApplicationRepository;
import com.hirsphere.backend.repository.CompanyRepository;
import com.hirsphere.backend.repository.JobRepository;
import com.hirsphere.backend.repository.UserRepository;
import com.hirsphere.backend.service.JobService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final ApplicationRepository applicationRepository;

    public JobServiceImpl(JobRepository jobRepository,
                          CompanyRepository companyRepository,
                          UserRepository userRepository,
                          ApplicationRepository applicationRepository) {
        this.jobRepository = jobRepository;
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
        this.applicationRepository = applicationRepository;
    }

    @Override
    @Transactional
    public JobDTO createJob(JobDTO dto, Long recruiterId) {
        User recruiter = userRepository.findById(recruiterId)
                .orElseThrow(() -> new ResourceNotFoundException("Recruiter not found"));

        Company company = companyRepository.findById(dto.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException("Company not found"));

        if (!company.getRecruiter().getId().equals(recruiterId)) {
            throw new IllegalArgumentException("You can only post jobs for your own company");
        }

        Job job = new Job();
        job.setTitle(dto.getTitle());
        job.setDescription(dto.getDescription());
        job.setLocation(dto.getLocation());
        job.setJobType(dto.getJobType());
        job.setExperienceLevel(dto.getExperienceLevel());
        job.setSalaryMin(dto.getSalaryMin());
        job.setSalaryMax(dto.getSalaryMax());
        job.setSkills(dto.getSkills());
        job.setRequirements(dto.getRequirements());
        job.setStatus(dto.getStatus() != null ? dto.getStatus() : JobStatus.ACTIVE);
        job.setCompany(company);
        job.setPostedBy(recruiter);
        job.setDeadline(dto.getDeadline());

        Job saved = jobRepository.save(job);
        return mapToDTO(saved);
    }

    @Override
    public JobDTO getJobById(Long id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + id));
        return mapToDTO(job);
    }

    @Override
    public List<JobDTO> getAllActiveJobs() {
        return jobRepository.findByStatus(JobStatus.ACTIVE)
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public List<JobDTO> getAllJobs() {
        return jobRepository.findAll()
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public JobDTO updateJobStatus(Long id, JobStatus status) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));
        job.setStatus(status);
        return mapToDTO(jobRepository.save(job));
    }

    @Override
    public List<JobDTO> getJobsByRecruiter(Long recruiterId) {
        return jobRepository.findByPostedById(recruiterId)
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public JobDTO updateJob(Long id, JobDTO dto, Long recruiterId) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));

        if (!job.getPostedBy().getId().equals(recruiterId)) {
            throw new IllegalArgumentException("You can only edit your own jobs");
        }

        job.setTitle(dto.getTitle());
        job.setDescription(dto.getDescription());
        job.setLocation(dto.getLocation());
        job.setJobType(dto.getJobType());
        job.setExperienceLevel(dto.getExperienceLevel());
        job.setSalaryMin(dto.getSalaryMin());
        job.setSalaryMax(dto.getSalaryMax());
        job.setSkills(dto.getSkills());
        job.setRequirements(dto.getRequirements());
        if (dto.getStatus() != null) job.setStatus(dto.getStatus());
        job.setDeadline(dto.getDeadline());

        return mapToDTO(jobRepository.save(job));
    }

    @Override
    @Transactional
    public void deleteJob(Long id, Long recruiterId) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));

        if (!job.getPostedBy().getId().equals(recruiterId)) {
            throw new IllegalArgumentException("You can only delete your own jobs");
        }

        jobRepository.delete(job);
    }

    @Override
    public List<JobDTO> searchJobs(String keyword, String location, JobType jobType) {
        return jobRepository.searchJobs(keyword, location, jobType)
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    private JobDTO mapToDTO(Job job) {
        JobDTO dto = new JobDTO();
        dto.setId(job.getId());
        dto.setTitle(job.getTitle());
        dto.setDescription(job.getDescription());
        dto.setLocation(job.getLocation());
        dto.setJobType(job.getJobType());
        dto.setExperienceLevel(job.getExperienceLevel());
        dto.setSalaryMin(job.getSalaryMin());
        dto.setSalaryMax(job.getSalaryMax());
        dto.setSkills(job.getSkills());
        dto.setRequirements(job.getRequirements());
        dto.setStatus(job.getStatus());
        dto.setCompanyId(job.getCompany().getId());
        dto.setCompanyName(job.getCompany().getName());
        dto.setCompanyLogo(job.getCompany().getLogoUrl());
        dto.setPostedById(job.getPostedBy().getId());
        dto.setPostedByName(job.getPostedBy().getName());
        dto.setDeadline(job.getDeadline());
        dto.setApplicationCount(applicationRepository.findByJobId(job.getId()).size());
        dto.setCreatedAt(job.getCreatedAt());
        dto.setUpdatedAt(job.getUpdatedAt());
        return dto;
    }
}
