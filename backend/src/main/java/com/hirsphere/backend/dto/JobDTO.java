package com.hirsphere.backend.dto;

import com.hirsphere.backend.entity.JobStatus;
import com.hirsphere.backend.entity.JobType;
import java.time.LocalDateTime;

public class JobDTO {

    private Long id;
    private String title;
    private String description;
    private Long companyId;
    private String companyName;
    private String companyLogo;
    private Long recruiterId;
    private String recruiterName;
    private Long postedById;
    private String postedByName;
    private String location;
    private JobType type;
    private String experienceLevel;
    private Double salaryMin;
    private Double salaryMax;
    private String skillsRequired;
    private String requirements;
    private JobStatus status;
    private LocalDateTime deadline;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private long applicationCount;

    public JobDTO() {}

    public JobDTO(Long id, String title, String description, Long companyId, String companyName, String companyLogo, Long recruiterId, String recruiterName, Long postedById, String postedByName, String location, JobType type, String experienceLevel, Double salaryMin, Double salaryMax, String skillsRequired, String requirements, JobStatus status, LocalDateTime deadline, LocalDateTime createdAt, LocalDateTime updatedAt, long applicationCount) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.companyId = companyId;
        this.companyName = companyName;
        this.companyLogo = companyLogo;
        this.recruiterId = recruiterId;
        this.recruiterName = recruiterName;
        this.postedById = postedById;
        this.postedByName = postedByName;
        this.location = location;
        this.type = type;
        this.experienceLevel = experienceLevel;
        this.salaryMin = salaryMin;
        this.salaryMax = salaryMax;
        this.skillsRequired = skillsRequired;
        this.requirements = requirements;
        this.status = status;
        this.deadline = deadline;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.applicationCount = applicationCount;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Long getCompanyId() { return companyId; }
    public void setCompanyId(Long companyId) { this.companyId = companyId; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getCompanyLogo() { return companyLogo; }
    public void setCompanyLogo(String companyLogo) { this.companyLogo = companyLogo; }

    public Long getRecruiterId() { return recruiterId; }
    public void setRecruiterId(Long recruiterId) { this.recruiterId = recruiterId; }

    public String getRecruiterName() { return recruiterName; }
    public void setRecruiterName(String recruiterName) { this.recruiterName = recruiterName; }

    public Long getPostedById() { return postedById; }
    public void setPostedById(Long postedById) { this.postedById = postedById; }

    public String getPostedByName() { return postedByName; }
    public void setPostedByName(String postedByName) { this.postedByName = postedByName; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public JobType getType() { return type; }
    public void setType(JobType type) { this.type = type; }

    public JobType getJobType() { return type; }
    public void setJobType(JobType type) { this.type = type; }

    public String getExperienceLevel() { return experienceLevel; }
    public void setExperienceLevel(String experienceLevel) { this.experienceLevel = experienceLevel; }

    public Double getSalaryMin() { return salaryMin; }
    public void setSalaryMin(Double salaryMin) { this.salaryMin = salaryMin; }

    public Double getSalaryMax() { return salaryMax; }
    public void setSalaryMax(Double salaryMax) { this.salaryMax = salaryMax; }

    public String getSkillsRequired() { return skillsRequired; }
    public void setSkillsRequired(String skillsRequired) { this.skillsRequired = skillsRequired; }

    public String getSkills() { return skillsRequired; }
    public void setSkills(String skillsRequired) { this.skillsRequired = skillsRequired; }

    public String getRequirements() { return requirements; }
    public void setRequirements(String requirements) { this.requirements = requirements; }

    public JobStatus getStatus() { return status; }
    public void setStatus(JobStatus status) { this.status = status; }

    public LocalDateTime getDeadline() { return deadline; }
    public void setDeadline(LocalDateTime deadline) { this.deadline = deadline; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public long getApplicationCount() { return applicationCount; }
    public void setApplicationCount(long applicationCount) { this.applicationCount = applicationCount; }
}
