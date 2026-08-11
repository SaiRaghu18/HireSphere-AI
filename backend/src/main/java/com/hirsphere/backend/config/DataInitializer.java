package com.hirsphere.backend.config;

import com.hirsphere.backend.entity.*;
import com.hirsphere.backend.repository.CompanyRepository;
import com.hirsphere.backend.repository.JobRepository;
import com.hirsphere.backend.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initData(
            UserRepository userRepository,
            CompanyRepository companyRepository,
            JobRepository jobRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {
            // Seed Admin
            if (!userRepository.existsByEmail("admin@hiresphere.ai")) {
                User admin = new User();
                admin.setName("Admin");
                admin.setEmail("admin@hiresphere.ai");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRole(UserRole.ADMIN);
                userRepository.save(admin);
            }

            // Seed Recruiter
            User recruiter = userRepository.findByEmail("recruiter@hiresphere.ai").orElse(null);
            if (recruiter == null) {
                recruiter = new User();
                recruiter.setName("Jane Doe");
                recruiter.setEmail("recruiter@hiresphere.ai");
                recruiter.setPassword(passwordEncoder.encode("recruiter123"));
                recruiter.setRole(UserRole.RECRUITER);
                recruiter = userRepository.save(recruiter);
            }

            // Seed Company
            Company company = companyRepository.findAll().stream().findFirst().orElse(null);
            if (company == null) {
                company = new Company();
                company.setName("TechCorp Solutions");
                company.setDescription("Leading software services and product innovation agency.");
                company.setWebsite("https://techcorp.example.com");
                company.setLocation("San Francisco, CA");
                company.setIndustry("Technology");
                company.setRecruiter(recruiter);
                company = companyRepository.save(company);
            }

            // Seed Jobs
            if (jobRepository.count() == 0) {
                Job job1 = new Job();
                job1.setTitle("Java Full Stack Developer");
                job1.setDescription("We are looking for an experienced Java developer with React expertise.");
                job1.setLocation("Remote");
                job1.setJobType(JobType.FULL_TIME);
                job1.setExperienceLevel("Senior");
                job1.setSalaryMin(110000.0);
                job1.setSalaryMax(140000.0);
                job1.setSkills("Java, Spring Boot, React, PostgreSQL");
                job1.setRequirements("5+ years of Java development, experience with modern React");
                job1.setStatus(JobStatus.ACTIVE);
                job1.setCompany(company);
                job1.setPostedBy(recruiter);
                job1.setDeadline(LocalDateTime.now().plusDays(30));
                jobRepository.save(job1);

                Job job2 = new Job();
                job2.setTitle("Software Engineer");
                job2.setDescription("Join our core backend systems team building high throughput web services.");
                job2.setLocation("Hybrid - Austin, TX");
                job2.setJobType(JobType.HYBRID);
                job2.setExperienceLevel("Mid Level");
                job2.setSalaryMin(95000.0);
                job2.setSalaryMax(120000.0);
                job2.setSkills("Java, Spring Boot, REST APIs");
                job2.setRequirements("3+ years of backend development experience");
                job2.setStatus(JobStatus.ACTIVE);
                job2.setCompany(company);
                job2.setPostedBy(recruiter);
                job2.setDeadline(LocalDateTime.now().plusDays(30));
                jobRepository.save(job2);

                Job job3 = new Job();
                job3.setTitle("React Developer");
                job3.setDescription("Build responsive and beautiful user interfaces using modern React features.");
                job3.setLocation("Remote");
                job3.setJobType(JobType.REMOTE);
                job3.setExperienceLevel("Mid Level");
                job3.setSalaryMin(90000.0);
                job3.setSalaryMax(115000.0);
                job3.setSkills("React, HTML5, CSS3, TailwindCSS");
                job3.setRequirements("Deep knowledge of React Hooks, CSS Grid/Flexbox");
                job3.setStatus(JobStatus.ACTIVE);
                job3.setCompany(company);
                job3.setPostedBy(recruiter);
                job3.setDeadline(LocalDateTime.now().plusDays(30));
                jobRepository.save(job3);
            }
        };
    }
}
