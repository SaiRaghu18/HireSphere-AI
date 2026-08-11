package com.hirsphere.backend.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hirsphere.backend.dto.ResumeAnalysisResponse;
import com.hirsphere.backend.entity.Resume;
import com.hirsphere.backend.entity.ResumeAnalysis;
import com.hirsphere.backend.exception.ResourceNotFoundException;
import com.hirsphere.backend.repository.ResumeAnalysisRepository;
import com.hirsphere.backend.repository.ResumeRepository;
import com.hirsphere.backend.service.AIResumeAnalyzer;
import com.hirsphere.backend.service.PdfTextExtractionService;
import com.hirsphere.backend.service.ResumeAnalysisService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.Optional;

@Service
public class ResumeAnalysisServiceImpl implements ResumeAnalysisService {

    private final ResumeRepository resumeRepository;
    private final ResumeAnalysisRepository resumeAnalysisRepository;
    private final PdfTextExtractionService pdfTextExtractionService;
    private final AIResumeAnalyzer aiResumeAnalyzer;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ResumeAnalysisServiceImpl(ResumeRepository resumeRepository,
                                     ResumeAnalysisRepository resumeAnalysisRepository,
                                     PdfTextExtractionService pdfTextExtractionService,
                                     AIResumeAnalyzer aiResumeAnalyzer) {
        this.resumeRepository = resumeRepository;
        this.resumeAnalysisRepository = resumeAnalysisRepository;
        this.pdfTextExtractionService = pdfTextExtractionService;
        this.aiResumeAnalyzer = aiResumeAnalyzer;
    }

    @Override
    @Transactional
    public ResumeAnalysisResponse analyzeResume(Long resumeId, Long candidateId) {
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new ResourceNotFoundException("Resume not found"));

        if (!resume.getUser().getId().equals(candidateId)) {
            throw new IllegalArgumentException("Access denied: You can only analyze your own resume");
        }

        File pdfFile = new File(resume.getFilePath());
        if (!pdfFile.exists()) {
            throw new ResourceNotFoundException("Physical resume file not found on server");
        }

        // 1. Extract text from PDF
        String extractedText = pdfTextExtractionService.extractText(pdfFile);

        // 2. Perform AI analysis
        ResumeAnalysisResponse response = aiResumeAnalyzer.analyzeResumeText(extractedText);

        // 3. Persist analysis to PostgreSQL database
        try {
            String jsonResult = objectMapper.writeValueAsString(response);
            Optional<ResumeAnalysis> existing = resumeAnalysisRepository.findByResumeId(resumeId);
            ResumeAnalysis analysis = existing.orElseGet(ResumeAnalysis::new);
            analysis.setResume(resume);
            analysis.setCandidate(resume.getUser());
            analysis.setJsonResult(jsonResult);
            resumeAnalysisRepository.save(analysis);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save resume analysis to database: " + e.getMessage());
        }

        return response;
    }

    @Override
    public ResumeAnalysisResponse getResumeAnalysis(Long resumeId, Long candidateId) {
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new ResourceNotFoundException("Resume not found"));

        if (!resume.getUser().getId().equals(candidateId)) {
            throw new IllegalArgumentException("Access denied: You can only view your own resume analysis");
        }

        ResumeAnalysis analysis = resumeAnalysisRepository.findByResumeId(resumeId)
                .orElseThrow(() -> new ResourceNotFoundException("Resume analysis has not been generated yet for this resume"));

        try {
            return objectMapper.readValue(analysis.getJsonResult(), ResumeAnalysisResponse.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse stored resume analysis: " + e.getMessage());
        }
    }
}
