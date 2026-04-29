package com.demo.service;

import com.demo.dto.EvaluationReportResponseDTO;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class PdfExportService {

    // fonts
    private static final Font FONT_TITLE      = new Font(Font.FontFamily.HELVETICA, 22, Font.BOLD, BaseColor.BLACK);
    private static final Font FONT_SECTION    = new Font(Font.FontFamily.HELVETICA, 13, Font.BOLD, new BaseColor(37, 99, 235));
    private static final Font FONT_LABEL      = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.DARK_GRAY);
    private static final Font FONT_VALUE      = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.BLACK);
    private static final Font FONT_SMALL_BOLD = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD, BaseColor.DARK_GRAY);
    private static final Font FONT_SMALL      = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL, BaseColor.DARK_GRAY);

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");

    // generates PDF bytes for the evaluation report
    public byte[] generateEvaluationPdf(EvaluationReportResponseDTO report) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4, 40, 40, 50, 50);
            PdfWriter.getInstance(document, out);
            document.open();

            // --- HEADER ---
            addHeader(document, report);

            // --- INTERVIEW INFO ---
            addSectionTitle(document, "Interview Details");
            addKeyValue(document, "Interview Type", report.getInterviewType());
            if (report.getTechStack() != null && !report.getTechStack().isEmpty()) {
                addKeyValue(document, "Tech Stack", String.join(", ", report.getTechStack()));
                addKeyValue(document, "Level", report.getInterviewLevel());
            }
            if (report.getStartTime() != null)
                addKeyValue(document, "Date", report.getStartTime().format(DATE_FORMAT));
            if (report.getStartTime() != null && report.getEndTime() != null) {
                long minutes = java.time.Duration.between(report.getStartTime(), report.getEndTime()).toMinutes();
                addKeyValue(document, "Duration", minutes + " minutes");
            }
            document.add(Chunk.NEWLINE);

            // --- SCORE SUMMARY ---
            addSectionTitle(document, "Score Summary");
            addScoreSummaryTable(document, report);
            document.add(Chunk.NEWLINE);

            // --- GPT FEEDBACK ---
            addSectionTitle(document, "Overall Feedback");
            addKeyValue(document, "Strengths", report.getStrengths());
            addKeyValue(document, "Areas to Improve", report.getAreasToImprove());
            addKeyValue(document, "Recommendation", report.getRecommendation());
            document.add(Chunk.NEWLINE);

            // --- PROCTORING SUMMARY ---
            addSectionTitle(document, "Proctoring Summary");
            addKeyValue(document, "Total Violations", String.valueOf(report.getTotalViolations()));
            document.add(Chunk.NEWLINE);

            // --- PER QUESTION BREAKDOWN ---
            addSectionTitle(document, "Question Breakdown");
            addQuestionBreakdown(document, report);

            // --- FOOTER ---
            addFooter(document);

            document.close();
            return out.toByteArray();

        } catch (Exception e) {
            log.error("PDF generation failed: {}", e.getMessage());
            throw new RuntimeException("Failed to generate PDF: " + e.getMessage());
        }
    }

    // -----------------------------------------------------------------------
    // HEADER
    // -----------------------------------------------------------------------
    private void addHeader(Document document, EvaluationReportResponseDTO report) throws DocumentException {
        Paragraph title = new Paragraph("AI Interview Evaluation Report", FONT_TITLE);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        Paragraph band = new Paragraph(
                report.getPerformanceBand() + "  |  Score: " + report.getOverallScore() + " / 10",
                new Font(Font.FontFamily.HELVETICA, 13, Font.BOLD, getBandColor(report.getPerformanceBand()))
        );
        band.setAlignment(Element.ALIGN_CENTER);
        band.setSpacingBefore(6);
        band.setSpacingAfter(16);
        document.add(band);

        LineSeparator line = new LineSeparator();
        line.setLineColor(new BaseColor(200, 200, 200));
        document.add(new Chunk(line));
        document.add(Chunk.NEWLINE);
    }

    // -----------------------------------------------------------------------
    // SCORE SUMMARY TABLE
    // -----------------------------------------------------------------------
    private void addScoreSummaryTable(Document document, EvaluationReportResponseDTO report) throws DocumentException {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(60);
        table.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.setSpacingBefore(6);

        addTableRow(table, "Overall Score", report.getOverallScore() + " / 10");
        addTableRow(table, "Performance Band", report.getPerformanceBand());
        addTableRow(table, "Total Questions", String.valueOf(report.getTotalQuestions()));
        addTableRow(table, "Answered", String.valueOf(report.getAnsweredQuestions()));
        addTableRow(table, "Skipped", String.valueOf(report.getSkippedQuestions()));

        document.add(table);
    }

    // -----------------------------------------------------------------------
    // QUESTION BREAKDOWN
    // -----------------------------------------------------------------------
    private void addQuestionBreakdown(Document document,
                                       EvaluationReportResponseDTO report) throws DocumentException {
        if (report.getQuestionBreakdown() == null) return;

        for (EvaluationReportResponseDTO.QuestionBreakdownDTO q : report.getQuestionBreakdown()) {
            // question header
            Paragraph qHeader = new Paragraph(
                    "Q" + q.getQuestionNumber() + (q.isFollowUp() ? " (Follow-up)" : "") + ": " + q.getQuestionText(),
                    FONT_LABEL
            );
            qHeader.setSpacingBefore(10);
            document.add(qHeader);

            // answer
            if (q.isWasSkipped()) {
                document.add(new Paragraph("Answer: [Skipped]", FONT_SMALL));
            } else {
                document.add(new Paragraph("Answer: " + q.getUserAnswer(), FONT_VALUE));
            }

            // scores table
            if (!q.isWasSkipped()) {
                PdfPTable scoreTable = new PdfPTable(5);
                scoreTable.setWidthPercentage(100);
                scoreTable.setSpacingBefore(4);

                addTableHeader(scoreTable, "Relevance", "Concept", "Clarity", "Grammar", "Completeness");
                addTableRow(scoreTable,
                        q.getRelevanceScore() + "/10",
                        q.getConceptScore() + "/10",
                        q.getClarityScore() + "/10",
                        q.getGrammarScore() + "/10",
                        q.getCompletenessScore() + "/10"
                );
                document.add(scoreTable);

                // feedback
                Paragraph feedback = new Paragraph("Feedback: " + q.getFeedbackSummary(), FONT_SMALL);
                feedback.setSpacingBefore(2);
                document.add(feedback);

                // response time
                if (q.getResponseTimeSeconds() != null) {
                    document.add(new Paragraph("Response Time: " + q.getResponseTimeSeconds() + "s", FONT_SMALL));
                }
            }
        }
    }

    // -----------------------------------------------------------------------
    // HELPERS
    // -----------------------------------------------------------------------
    private void addSectionTitle(Document document, String title) throws DocumentException {
        Paragraph p = new Paragraph(title, FONT_SECTION);
        p.setSpacingBefore(14);
        p.setSpacingAfter(6);
        document.add(p);
    }

    private void addKeyValue(Document document, String label, String value) throws DocumentException {
        Paragraph p = new Paragraph();
        p.add(new Chunk(label + ": ", FONT_LABEL));
        p.add(new Chunk(value != null ? value : "N/A", FONT_VALUE));
        p.setSpacingBefore(3);
        document.add(p);
    }

    private void addTableRow(PdfPTable table, String... values) {
        for (String val : values) {
            PdfPCell cell = new PdfPCell(new Phrase(val, FONT_VALUE));
            cell.setPadding(5);
            cell.setBorderColor(new BaseColor(220, 220, 220));
            table.addCell(cell);
        }
    }

    private void addTableHeader(PdfPTable table, String... headers) {
        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(h, FONT_SMALL_BOLD));
            cell.setPadding(5);
            cell.setBackgroundColor(new BaseColor(240, 240, 240));
            cell.setBorderColor(new BaseColor(200, 200, 200));
            table.addCell(cell);
        }
    }

    private void addFooter(Document document) throws DocumentException {
        document.add(Chunk.NEWLINE);
        LineSeparator line = new LineSeparator();
        line.setLineColor(new BaseColor(200, 200, 200));
        document.add(new Chunk(line));
        Paragraph footer = new Paragraph("Generated by AI Interviewer  •  Confidential", FONT_SMALL);
        footer.setAlignment(Element.ALIGN_CENTER);
        footer.setSpacingBefore(6);
        document.add(footer);
    }

    // color based on performance band
    private BaseColor getBandColor(String band) {
        return switch (band) {
            case "Excellent"     -> new BaseColor(22, 163, 74);   // green
            case "Good"          -> new BaseColor(37, 99, 235);   // blue
            case "Average"       -> new BaseColor(234, 179, 8);   // yellow
            case "Below Average" -> new BaseColor(249, 115, 22);  // orange
            default              -> new BaseColor(220, 38, 38);   // red
        };
    }
}