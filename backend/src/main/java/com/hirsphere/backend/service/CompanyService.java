package com.hirsphere.backend.service;

import com.hirsphere.backend.dto.CompanyDTO;

import java.util.List;

public interface CompanyService {
    CompanyDTO createCompany(CompanyDTO dto, Long recruiterId);
    CompanyDTO getCompanyById(Long id);
    List<CompanyDTO> getCompaniesByRecruiter(Long recruiterId);
    List<CompanyDTO> getAllCompanies();
    CompanyDTO updateCompany(Long id, CompanyDTO dto, Long recruiterId);
}
