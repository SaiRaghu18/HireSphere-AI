package com.hirsphere.backend.service.impl;

import com.hirsphere.backend.dto.DashboardStats;
import com.hirsphere.backend.dto.UserDTO;
import com.hirsphere.backend.entity.ApplicationStatus;
import com.hirsphere.backend.entity.JobStatus;
import com.hirsphere.backend.entity.User;
import com.hirsphere.backend.entity.UserRole;
import com.hirsphere.backend.exception.ResourceNotFoundException;
import com.hirsphere.backend.repository.ApplicationRepository;
import com.hirsphere.backend.repository.JobRepository;
import com.hirsphere.backend.repository.UserRepository;
import com.hirsphere.backend.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final com.hirsphere.backend.repository.CompanyRepository companyRepository;

    public UserServiceImpl(UserRepository userRepository,
                           ApplicationRepository applicationRepository,
                           JobRepository jobRepository,
                           com.hirsphere.backend.repository.CompanyRepository companyRepository) {
        this.userRepository = userRepository;
        this.applicationRepository = applicationRepository;
        this.jobRepository = jobRepository;
        this.companyRepository = companyRepository;
    }

    @Override
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return mapToDTO(user);
    }

    @Override
    public UserDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return mapToDTO(user);
    }

    @Override
    @Transactional
    public UserDTO updateProfile(Long id, UserDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (dto.getName() != null) user.setName(dto.getName());
        if (dto.getPhone() != null) user.setPhone(dto.getPhone());
        if (dto.getSkills() != null) user.setSkills(dto.getSkills());
        if (dto.getTitle() != null) user.setTitle(dto.getTitle());
        if (dto.getBio() != null) user.setBio(dto.getBio());
        if (dto.getLocation() != null) user.setLocation(dto.getLocation());
        if (dto.getCollege() != null) user.setCollege(dto.getCollege());
        if (dto.getGraduationYear() != null) user.setGraduationYear(dto.getGraduationYear());
        if (dto.getExperience() != null) user.setExperience(dto.getExperience());
        if (dto.getEducation() != null) user.setEducation(dto.getEducation());
        if (dto.getGithubUrl() != null) user.setGithubUrl(dto.getGithubUrl());
        if (dto.getLinkedinUrl() != null) user.setLinkedinUrl(dto.getLinkedinUrl());

        return mapToDTO(userRepository.save(user));
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public List<UserDTO> getUsersByRole(String role) {
        UserRole userRole = UserRole.valueOf(role.toUpperCase());
        return userRepository.findByRole(userRole).stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found");
        }
        userRepository.deleteById(id);
    }

    @Override
    @Transactional
    public UserDTO updateUserStatus(Long id, String status) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setStatus(status);
        return mapToDTO(userRepository.save(user));
    }

    @Override
    public DashboardStats getJobSeekerStats(Long userId) {
        DashboardStats stats = new DashboardStats();
        stats.setTotalApplications(applicationRepository.countByApplicantId(userId));
        stats.setShortlisted(applicationRepository.countByApplicantIdAndStatus(userId, ApplicationStatus.SHORTLISTED));
        stats.setInterviews(applicationRepository.countByApplicantIdAndStatus(userId, ApplicationStatus.INTERVIEW));
        stats.setRejected(applicationRepository.countByApplicantIdAndStatus(userId, ApplicationStatus.REJECTED));
        stats.setHired(applicationRepository.countByApplicantIdAndStatus(userId, ApplicationStatus.HIRED));
        return stats;
    }

    @Override
    public DashboardStats getRecruiterStats(Long recruiterId) {
        DashboardStats stats = new DashboardStats();
        stats.setTotalJobs(jobRepository.countByPostedById(recruiterId));
        stats.setActiveJobs(jobRepository.findByPostedById(recruiterId).stream().filter(j -> j.getStatus() == JobStatus.ACTIVE).count());
        stats.setTotalApplicants(applicationRepository.countByJobPostedById(recruiterId));
        stats.setShortlisted(applicationRepository.countByJobPostedByIdAndStatus(recruiterId, ApplicationStatus.SHORTLISTED));
        stats.setInterviews(applicationRepository.countByJobPostedByIdAndStatus(recruiterId, ApplicationStatus.INTERVIEW));
        stats.setHired(applicationRepository.countByJobPostedByIdAndStatus(recruiterId, ApplicationStatus.HIRED));
        stats.setRejected(applicationRepository.countByJobPostedByIdAndStatus(recruiterId, ApplicationStatus.REJECTED));
        return stats;
    }

    @Override
    public DashboardStats getAdminStats() {
        DashboardStats stats = new DashboardStats();
        stats.setTotalUsers(userRepository.count());
        long jobSeekers = userRepository.countByRole(UserRole.JOB_SEEKER);
        stats.setTotalJobSeekers(jobSeekers);
        stats.setTotalCandidates(jobSeekers);
        stats.setTotalRecruiters(userRepository.countByRole(UserRole.RECRUITER));
        stats.setTotalCompanies(companyRepository.count());
        stats.setTotalJobs(jobRepository.count());
        long activeCount = jobRepository.countByStatus(JobStatus.ACTIVE);
        stats.setActiveJobs(activeCount);
        stats.setActiveJobsCount(activeCount);
        stats.setTotalApplications(applicationRepository.count());
        stats.setShortlistedApplications(applicationRepository.countByStatus(ApplicationStatus.SHORTLISTED));
        stats.setTotalInterviews(applicationRepository.countByStatus(ApplicationStatus.INTERVIEW));
        stats.setTotalHired(applicationRepository.countByStatus(ApplicationStatus.HIRED));
        return stats;
    }

    private UserDTO mapToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setRole(user.getRole());
        dto.setStatus(user.getStatus() != null ? user.getStatus() : "ACTIVE");
        dto.setSkills(user.getSkills());
        dto.setTitle(user.getTitle());
        dto.setBio(user.getBio());
        dto.setLocation(user.getLocation());
        dto.setCollege(user.getCollege());
        dto.setGraduationYear(user.getGraduationYear());
        dto.setExperience(user.getExperience());
        dto.setEducation(user.getEducation());
        dto.setGithubUrl(user.getGithubUrl());
        dto.setLinkedinUrl(user.getLinkedinUrl());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }
}
