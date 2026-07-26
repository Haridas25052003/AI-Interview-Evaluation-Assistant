package com.demo.evaluation.controller;

import com.demo.evaluation.dto.EvaluationReportResponseDTO;
import com.demo.evaluation.repository.AnalysisResultRepository;
import com.demo.interview.repository.InterviewQuestionHistoryRepository;
import com.demo.interview.repository.InterviewSessionRepository;
import com.demo.evaluation.service.EvaluationService;
import com.demo.evaluation.service.PdfExportService;
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
    private final InterviewSessionRepository sessionRepository;
    private final InterviewQuestionHistoryRepository questionHistoryRepository;
    private final AnalysisResultRepository analysisResultRepository;

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