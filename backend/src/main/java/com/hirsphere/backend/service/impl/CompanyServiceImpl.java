package com.hirsphere.backend.service.impl;

import com.hirsphere.backend.dto.CompanyDTO;
import com.hirsphere.backend.entity.Company;
import com.hirsphere.backend.entity.User;
import com.hirsphere.backend.exception.ResourceNotFoundException;
import com.hirsphere.backend.repository.CompanyRepository;
import com.hirsphere.backend.repository.UserRepository;
import com.hirsphere.backend.service.CompanyService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final com.hirsphere.backend.repository.JobRepository jobRepository;

    public CompanyServiceImpl(CompanyRepository companyRepository, 
                              UserRepository userRepository,
                              com.hirsphere.backend.repository.JobRepository jobRepository) {
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
    }

    @Override
    @Transactional
    public CompanyDTO createCompany(CompanyDTO dto, Long recruiterId) {
        User recruiter = userRepository.findById(recruiterId)
                .orElseThrow(() -> new ResourceNotFoundException("Recruiter not found"));

        Company company = new Company();
        company.setName(dto.getName());
        company.setDescription(dto.getDescription());
        company.setWebsite(dto.getWebsite());
        company.setLocation(dto.getLocation());
        company.setIndustry(dto.getIndustry());
        company.setSize(dto.getSize());
        company.setLogoUrl(dto.getLogoUrl());
        company.setRecruiter(recruiter);

        return mapToDTO(companyRepository.save(company));
    }

    @Override
    public CompanyDTO getCompanyById(Long id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found"));
        return mapToDTO(company);
    }

    @Override
    public List<CompanyDTO> getCompaniesByRecruiter(Long recruiterId) {
        return companyRepository.findByRecruiterId(recruiterId)
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public List<CompanyDTO> getAllCompanies() {
        return companyRepository.findAll()
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CompanyDTO updateCompany(Long id, CompanyDTO dto, Long recruiterId) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found"));

        if (!company.getRecruiter().getId().equals(recruiterId)) {
            throw new IllegalArgumentException("You can only edit your own company");
        }

        company.setName(dto.getName());
        company.setDescription(dto.getDescription());
        company.setWebsite(dto.getWebsite());
        company.setLocation(dto.getLocation());
        company.setIndustry(dto.getIndustry());
        company.setSize(dto.getSize());
        company.setLogoUrl(dto.getLogoUrl());

        return mapToDTO(companyRepository.save(company));
    }

    private CompanyDTO mapToDTO(Company c) {
        CompanyDTO dto = new CompanyDTO();
        dto.setId(c.getId());
        dto.setName(c.getName());
        dto.setDescription(c.getDescription());
        dto.setWebsite(c.getWebsite());
        dto.setLocation(c.getLocation());
        dto.setIndustry(c.getIndustry());
        dto.setSize(c.getSize());
        dto.setLogoUrl(c.getLogoUrl());
        if (c.getRecruiter() != null) {
            dto.setRecruiterId(c.getRecruiter().getId());
            dto.setRecruiterName(c.getRecruiter().getName());
        }
        dto.setJobCount(jobRepository.findByCompanyId(c.getId()).size());
        dto.setCreatedAt(c.getCreatedAt());
        return dto;
    }
}
