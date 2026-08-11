package com.hirsphere.backend.service.impl;

import com.hirsphere.backend.exception.ResourceNotFoundException;
import com.hirsphere.backend.service.PdfTextExtractionService;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
public class PdfTextExtractionServiceImpl implements PdfTextExtractionService {

    @Override
    public String extractText(File pdfFile) {
        if (pdfFile == null || !pdfFile.exists()) {
            throw new ResourceNotFoundException("PDF file not found");
        }

        if (!pdfFile.getName().toLowerCase().endsWith(".pdf")) {
            throw new IllegalArgumentException("File must be a PDF document");
        }

        try (PDDocument document = Loader.loadPDF(pdfFile)) {
            if (document.isEncrypted()) {
                throw new IllegalArgumentException("Encrypted PDF files are not supported");
            }

            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);

            if (text == null || text.trim().isEmpty()) {
                throw new IllegalArgumentException("The PDF file contains no readable text");
            }

            return text.trim();
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to read or parse PDF file: " + e.getMessage());
        }
    }
}
