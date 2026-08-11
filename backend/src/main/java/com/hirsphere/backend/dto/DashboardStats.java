package com.hirsphere.backend.dto;

public class DashboardStats {

    // Job Seeker stats
    private long totalApplications;
    private long shortlisted;
    private long interviews;
    private long rejected;
    private long hired;

    // Recruiter stats
    private long totalJobs;
    private long activeJobs;
    private long totalApplicants;

    // Admin stats
    private long totalUsers;
    private long totalRecruiters;
    private long totalJobSeekers;
    private long totalCandidates;
    private long totalCompanies;
    private long activeJobsCount;
    private long shortlistedApplications;
    private long totalInterviews;
    private long totalHired;

    public DashboardStats() {}

    public DashboardStats(long totalApplications, long shortlisted, long interviews, long rejected, long hired, long totalJobs, long activeJobs, long totalApplicants, long totalUsers, long totalRecruiters, long totalJobSeekers, long totalCandidates, long totalCompanies, long activeJobsCount, long shortlistedApplications, long totalInterviews, long totalHired) {
        this.totalApplications = totalApplications;
        this.shortlisted = shortlisted;
        this.interviews = interviews;
        this.rejected = rejected;
        this.hired = hired;
        this.totalJobs = totalJobs;
        this.activeJobs = activeJobs;
        this.totalApplicants = totalApplicants;
        this.totalUsers = totalUsers;
        this.totalRecruiters = totalRecruiters;
        this.totalJobSeekers = totalJobSeekers;
        this.totalCandidates = totalCandidates;
        this.totalCompanies = totalCompanies;
        this.activeJobsCount = activeJobsCount;
        this.shortlistedApplications = shortlistedApplications;
        this.totalInterviews = totalInterviews;
        this.totalHired = totalHired;
    }

    public long getTotalApplications() { return totalApplications; }
    public void setTotalApplications(long totalApplications) { this.totalApplications = totalApplications; }

    public long getShortlisted() { return shortlisted; }
    public void setShortlisted(long shortlisted) { this.shortlisted = shortlisted; }

    public long getInterviews() { return interviews; }
    public void setInterviews(long interviews) { this.interviews = interviews; }

    public long getRejected() { return rejected; }
    public void setRejected(long rejected) { this.rejected = rejected; }

    public long getHired() { return hired; }
    public void setHired(long hired) { this.hired = hired; }

    public long getTotalJobs() { return totalJobs; }
    public void setTotalJobs(long totalJobs) { this.totalJobs = totalJobs; }

    public long getActiveJobs() { return activeJobs; }
    public void setActiveJobs(long activeJobs) { this.activeJobs = activeJobs; }

    public long getTotalApplicants() { return totalApplicants; }
    public void setTotalApplicants(long totalApplicants) { this.totalApplicants = totalApplicants; }

    public long getTotalUsers() { return totalUsers; }
    public void setTotalUsers(long totalUsers) { this.totalUsers = totalUsers; }

    public long getTotalRecruiters() { return totalRecruiters; }
    public void setTotalRecruiters(long totalRecruiters) { this.totalRecruiters = totalRecruiters; }

    public long getTotalJobSeekers() { return totalJobSeekers; }
    public void setTotalJobSeekers(long totalJobSeekers) { this.totalJobSeekers = totalJobSeekers; }

    public long getTotalCandidates() { return totalCandidates; }
    public void setTotalCandidates(long totalCandidates) { this.totalCandidates = totalCandidates; }

    public long getTotalCompanies() { return totalCompanies; }
    public void setTotalCompanies(long totalCompanies) { this.totalCompanies = totalCompanies; }

    public long getActiveJobsCount() { return activeJobsCount; }
    public void setActiveJobsCount(long activeJobsCount) { this.activeJobsCount = activeJobsCount; }

    public long getShortlistedApplications() { return shortlistedApplications; }
    public void setShortlistedApplications(long shortlistedApplications) { this.shortlistedApplications = shortlistedApplications; }

    public long getTotalInterviews() { return totalInterviews; }
    public void setTotalInterviews(long totalInterviews) { this.totalInterviews = totalInterviews; }

    public long getTotalHired() { return totalHired; }
    public void setTotalHired(long totalHired) { this.totalHired = totalHired; }
}
