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
    @Autowired
    private final ProblemService problemService;
    @Autowired
    private final CategoryService categoryService;
    @Autowired
    private final UserService userService;
    @Autowired
    private final JwtService jwtService;
    @PersistenceContext
    private final EntityManager entityManager;
    @Autowired
    private final PhotoService photoService;
    @Autowired
    private final GeoConversionService geoConversionService;

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
    public ResponseEntity<?> createReport(@RequestBody CreateReportRequestDto reportRequest, HttpServletRequest httpRequest) throws JsonProcessingException {

        reportRequest = reportService.validateLocation(reportRequest);
        if (reportRequest == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No address, photo or coordinates given.");
        }

        Optional<Category> optionalCategory = categoryService.getCategoryById(reportRequest.getCategoryId());
        if (optionalCategory.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Category not found");
        }
        Category category = optionalCategory.get();

        User user = null;
        String authorizationHeader = httpRequest.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            Integer userId = jwtService.extractUserId(token);
            Optional<User> optionalUser = userService.getUserById(userId);
            if (optionalUser.isEmpty()) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Token user not found");
            }
            user = optionalUser.get();
        }

        Problem savedProblem = null;
        if (reportRequest.getMergeProblemId() == null) {
            Problem problem = Problem.builder()
                    .longitude(reportRequest.getLongitude())
                    .latitude(reportRequest.getLatitude())
                    .status(reportRequest.getProblemStatus())
                    .category(category)
                    .build();

            // Save the Problem object
            savedProblem = problemService.createProblem(problem);
            if (savedProblem == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Problem object cannot be initialized");
            }
        } else {
            Optional<Problem> optionalProblem = problemService.getProblemById(reportRequest.getMergeProblemId());
            if (optionalProblem.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nearby problem couldn't be found");
            }
            savedProblem = optionalProblem.get();
        }

        // Create photos
        List<Photo> photos = new ArrayList<>();
        if (reportRequest.getBase64Photos() != null) {
            for (String base64Photo : reportRequest.getBase64Photos()) {
                Photo photo = Photo.builder()
                        .photoData(Base64.decodeBase64(base64Photo))
                        .report(null)
                        .build();
                photoService.createPhoto(photo);
                photos.add(photo);
            }
        }

        // Build a new Report
        Report report = Report.builder()
                .user(user)
                .title(reportRequest.getTitle())
                .description(reportRequest.getDescription())
                .address(reportRequest.getAddress())
                .base64Photos(reportRequest.getBase64Photos())
                .status(reportRequest.getReportStatus())
                .latitude(reportRequest.getLatitude())
                .longitude(reportRequest.getLongitude())
                .problem(savedProblem)
                .build();

        Report savedReport = reportService.createReport(report);

        for (Photo photo : photos) {
            photo.setReport(savedReport);
            photoService.updatePhoto(photo.getPhotoId(), photo);
        }
        entityManager.refresh(savedReport);
        savedReport.setPhotos(photos);
        reportService.createReport(savedReport);

        return ResponseEntity.ok(savedReport);
    }

    @Operation(summary = "See if there is a nearbyReport")
    @PostMapping("/public/nearbyReport")
    public ResponseEntity<Integer> getNearbyReport(@RequestBody CreateReportRequestDto reportRequest) throws JsonProcessingException {
        return ResponseEntity.ok(reportService.getNearbyReport(reportRequest));
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
