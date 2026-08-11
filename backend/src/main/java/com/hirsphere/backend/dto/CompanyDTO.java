package com.hirsphere.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompanyDTO {

    private Long id;

    @NotBlank(message = "Company name is required")
    private String name;

    private String description;
    private String website;
    private String location;
    private String industry;
    private String size;
    private String logoUrl;
    private Long recruiterId;
    private String recruiterName;
    private long jobCount;
    private LocalDateTime createdAt;
}
