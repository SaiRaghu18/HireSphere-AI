package com.hirsphere.backend.dto;

import com.hirsphere.backend.entity.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationStatusUpdateDTO {
    private ApplicationStatus status;
}
