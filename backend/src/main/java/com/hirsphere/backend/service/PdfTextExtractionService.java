package com.hirsphere.backend.service;

import java.io.File;

public interface PdfTextExtractionService {
    String extractText(File pdfFile);
}
