package com.hirsphere.backend.repository;

import com.hirsphere.backend.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {

    List<Company> findByRecruiterId(Long recruiterId);

    Optional<Company> findByRecruiterIdAndId(Long recruiterId, Long id);

    Optional<Company> findFirstByRecruiterId(Long recruiterId);
}
