package com.hirsphere.backend.dto;

import java.util.List;

public class ResumeAnalysisResponse {

    private String candidateName;
    private String summary;
    private List<String> skills;
    private List<String> programmingLanguages;
    private List<String> frameworks;
    private List<String> databases;
    private List<String> tools;
    private Integer yearsOfExperience;
    private List<String> education;
    private List<String> certifications;
    private List<String> projects;
    private List<String> strengths;
    private List<String> improvementAreas;

    public ResumeAnalysisResponse() {}

    public ResumeAnalysisResponse(String candidateName, String summary, List<String> skills, List<String> programmingLanguages, List<String> frameworks, List<String> databases, List<String> tools, Integer yearsOfExperience, List<String> education, List<String> certifications, List<String> projects, List<String> strengths, List<String> improvementAreas) {
        this.candidateName = candidateName;
        this.summary = summary;
        this.skills = skills;
        this.programmingLanguages = programmingLanguages;
        this.frameworks = frameworks;
        this.databases = databases;
        this.tools = tools;
        this.yearsOfExperience = yearsOfExperience;
        this.education = education;
        this.certifications = certifications;
        this.projects = projects;
        this.strengths = strengths;
        this.improvementAreas = improvementAreas;
    }

    public String getCandidateName() { return candidateName; }
    public void setCandidateName(String candidateName) { this.candidateName = candidateName; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public List<String> getSkills() { return skills; }
    public void setSkills(List<String> skills) { this.skills = skills; }

    public List<String> getProgrammingLanguages() { return programmingLanguages; }
    public void setProgrammingLanguages(List<String> programmingLanguages) { this.programmingLanguages = programmingLanguages; }

    public List<String> getFrameworks() { return frameworks; }
    public void setFrameworks(List<String> frameworks) { this.frameworks = frameworks; }

    public List<String> getDatabases() { return databases; }
    public void setDatabases(List<String> databases) { this.databases = databases; }

    public List<String> getTools() { return tools; }
    public void setTools(List<String> tools) { this.tools = tools; }

    public Integer getYearsOfExperience() { return yearsOfExperience; }
    public void setYearsOfExperience(Integer yearsOfExperience) { this.yearsOfExperience = yearsOfExperience; }

    public List<String> getEducation() { return education; }
    public void setEducation(List<String> education) { this.education = education; }

    public List<String> getCertifications() { return certifications; }
    public void setCertifications(List<String> certifications) { this.certifications = certifications; }

    public List<String> getProjects() { return projects; }
    public void setProjects(List<String> projects) { this.projects = projects; }

    public List<String> getStrengths() { return strengths; }
    public void setStrengths(List<String> strengths) { this.strengths = strengths; }

    public List<String> getImprovementAreas() { return improvementAreas; }
    public void setImprovementAreas(List<String> improvementAreas) { this.improvementAreas = improvementAreas; }
}
