package com.hirsphere.backend.repository;

import com.hirsphere.backend.entity.Job;
import com.hirsphere.backend.entity.JobStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long> {

    List<Job> findByStatus(JobStatus status);

    List<Job> findByPostedById(Long recruiterId);

    List<Job> findByCompanyId(Long companyId);

    @Query("SELECT j FROM Job j WHERE j.status = com.hirsphere.backend.entity.JobStatus.ACTIVE AND " +
           "(LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(j.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(j.skillsRequired) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Job> searchByKeyword(@Param("keyword") String keyword);

    @Query("SELECT j FROM Job j WHERE j.status = com.hirsphere.backend.entity.JobStatus.ACTIVE AND " +
           "(:keyword IS NULL OR LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(j.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(j.skillsRequired) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(:location IS NULL OR LOWER(j.location) LIKE LOWER(CONCAT('%', :location, '%'))) AND " +
           "(:jobType IS NULL OR j.type = :jobType)")
    List<Job> searchJobs(@Param("keyword") String keyword,
                         @Param("location") String location,
                         @Param("jobType") com.hirsphere.backend.entity.JobType jobType);

    long countByStatus(JobStatus status);

    long countByPostedById(Long recruiterId);
}
