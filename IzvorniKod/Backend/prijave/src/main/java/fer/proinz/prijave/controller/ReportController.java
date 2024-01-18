package fer.proinz.prijave.controller;

import com.drew.imaging.ImageProcessingException;
import fer.proinz.prijave.dto.ReportRequestDto;
import fer.proinz.prijave.exception.NonExistingCategoryException;
import fer.proinz.prijave.exception.NonExistingProblemException;
import fer.proinz.prijave.exception.NonExistingReportException;
import fer.proinz.prijave.exception.NotEnoughDataException;
import fer.proinz.prijave.model.*;
import fer.proinz.prijave.service.*;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class ReportController {
    @Autowired
    private final ReportService reportService;

    @Operation(summary = "Get all reports")
    @GetMapping( "/public/report/getAll")
    public List<Report> getAllReports() throws NonExistingReportException {
        return reportService.getAllReports();
    }

    @Operation(summary = "Get a report by it's report_id")
    @GetMapping( "/public/report/{reportId}")
    public ResponseEntity<Report> getReportById(@PathVariable("reportId") int reportId)
            throws NonExistingReportException {
        return ResponseEntity.ok(reportService
                .getReportById(reportId)
                .orElseThrow(NonExistingReportException::new)
        );
    }

    @Operation(summary = "Get a report by it's business_id")
    @GetMapping("/public/lookup/{businessId}")
    public ResponseEntity<Report> getReportByBusinessId(@PathVariable("businessId") UUID businessId)
            throws NonExistingReportException {
        return ResponseEntity.ok(reportService.getReportByBusinessId(businessId));
    }

    @Operation(summary = "Get statistics")
    @GetMapping("/public/statistics")
    public Map<String, Integer> reportStatistics() {
        return reportService.getReportsStatistics();
    }

    @Operation(summary = "Create a report")
    @PostMapping("/public/report")
    @Transactional
    public ResponseEntity<?> createReport(@RequestBody ReportRequestDto reportRequest)
            throws IOException, ImageProcessingException, NonExistingCategoryException,
            NonExistingProblemException, NotEnoughDataException {
        return ResponseEntity.ok(reportService.createReport(reportRequest));
    }

    @Operation(summary = "See if there is a nearbyReport")
    @PostMapping("/public/nearbyReport")
    public ResponseEntity<?> getNearbyReport(@RequestBody ReportRequestDto reportRequest)
            throws IOException, ImageProcessingException, NotEnoughDataException {
        Integer result = reportService.getNearbyReport(reportRequest);
        if (result == null) {
            throw new NotEnoughDataException();
        } else {
            return ResponseEntity.ok(reportService.getNearbyReport(reportRequest));
        }
    }

    @Operation(summary = "Update a report")
    @PatchMapping("/advanced/report/{reportId}")
    public ResponseEntity<?> updateReport(
            @PathVariable("reportId") int reportId,
            @RequestBody Report updatedReport
        ) throws NonExistingReportException {
        return reportService.updateReport(reportId, updatedReport);
    }

    @Operation(summary = "Delete a report")
    @DeleteMapping("/advanced/report/{reportId}")
    public ResponseEntity<String> deleteReport(@PathVariable("reportId") int reportId)
            throws NonExistingReportException, NonExistingProblemException {
        return reportService.deleteReport(reportId);
    }

}
