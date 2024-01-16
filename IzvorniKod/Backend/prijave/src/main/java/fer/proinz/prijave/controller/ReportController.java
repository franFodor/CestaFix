package fer.proinz.prijave.controller;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.lang.GeoLocation;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.GpsDirectory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fer.proinz.prijave.dto.CreateReportRequestDto;
import fer.proinz.prijave.model.*;
import fer.proinz.prijave.service.*;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.apache.tomcat.util.codec.binary.Base64;

import java.io.ByteArrayInputStream;
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
    public List<Report> getAllReports() {
        return reportService.getAllReports();
    }

    @Operation(summary = "Get a report by it's report_id")
    @GetMapping( "/public/report/{reportId}")
    public ResponseEntity<Report> getReportById(@PathVariable("reportId") int reportId) {
        return reportService.getReportById(reportId);
    }

    @Operation(summary = "Get a report by it's business_id")
    @GetMapping("/public/lookup/{businessId}")
    public ResponseEntity<Report> getReportByBusinessId(@PathVariable("businessId") UUID businessId) {
        return reportService.getReportByBusinessId(businessId);
    }

    @Operation(summary = "Get statistics")
    @GetMapping("/public/statistics")
    public Map<String, Integer> reportStatistics() {
        return reportService.getReportsStatistics();
    }

    @Operation(summary = "Create a report")
    @PostMapping("/public/report")
    @Transactional
    public ResponseEntity<?> createReport(
            @RequestBody CreateReportRequestDto reportRequest,
            HttpServletRequest httpRequest
    ) throws JsonProcessingException {
        return ResponseEntity.ok(reportService.createReport(reportRequest, httpRequest));
    }

    @Operation(summary = "See if there is a nearbyReport")
    @PostMapping("/public/nearbyReport")
    public ResponseEntity<?> getNearbyReport(@RequestBody CreateReportRequestDto reportRequest) throws JsonProcessingException {
        Integer result = reportService.getNearbyReport(reportRequest);
        if (result == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No address, photo or coordinates given.");
        } else {
            return ResponseEntity.ok(reportService.getNearbyReport(reportRequest));
        }
    }

    @Operation(summary = "Update a report")
    @PatchMapping("/advanced/report/{reportId}")
    public ResponseEntity<?> updateReport(
            @PathVariable("reportId") int reportId,
            @RequestBody Report updatedReport
        ) {
        return reportService.updateReport(reportId, updatedReport);
    }

    @Operation(summary = "Staff member groups reports")
    @PatchMapping("/advanced/report/group/{problemId}")
    public ResponseEntity<?> groupReports(
            @PathVariable("problemId") int problemId,
            @RequestBody List<Integer> reportIdList
    ) {
        return reportService.groupReports(problemId, reportIdList);
    }

    @Operation(summary = "Delete a report")
    @DeleteMapping("/advanced/report/{reportId}")
    public ResponseEntity<String> deleteReport(@PathVariable("reportId") int reportId) {
        return reportService.deleteReport(reportId);
    }

}
