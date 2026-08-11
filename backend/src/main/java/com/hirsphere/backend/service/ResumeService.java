package com.hirsphere.backend.service;

import com.hirsphere.backend.entity.Resume;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface ResumeService {
    Resume uploadResume(MultipartFile file, Long userId);
    Resume getResumeByUserId(Long userId);
    byte[] downloadResume(Long userId);
    void deleteResume(Long id, Long userId);
    Resume getResumeById(Long id);
}
