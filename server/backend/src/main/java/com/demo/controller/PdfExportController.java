package com.demo.controller;

import com.demo.dto.EvaluationReportResponseDTO;
import com.demo.service.EvaluationService;
import com.demo.service.PdfExportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/evaluation")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PdfExportController {

    private final PdfExportService pdfExportService;
    private final EvaluationService evaluationService;
    private final com.demo.repository.InterviewSessionRepository sessionRepository;
    private final com.demo.repository.InterviewQuestionHistoryRepository questionHistoryRepository;
    private final com.demo.repository.AnalysisResultRepository analysisResultRepository;

    // GET /api/evaluation/download/{sessionToken}
    // frontend calls this when user clicks "Download Report" button
    // returns PDF as a file download
    @GetMapping("/download/{sessionToken}")
    public ResponseEntity<byte[]> downloadReport(@PathVariable String sessionToken) {

        // fetch the full report DTO (reuse evaluation controller mapping logic)
        EvaluationReportResponseDTO reportDTO = evaluationService.getReportDTO(sessionToken);

        // generate PDF bytes
        byte[] pdfBytes = pdfExportService.generateEvaluationPdf(reportDTO);

        // set filename as: report-{sessionToken}.pdf
        String filename = "interview-report-" + sessionToken.substring(0, 8) + ".pdf";

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(pdfBytes);
    }
}