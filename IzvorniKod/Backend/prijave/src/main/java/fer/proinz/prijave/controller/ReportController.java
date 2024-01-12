package fer.proinz.prijave.controller;

import com.fasterxml.jackson.core.JsonParser;
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
import org.antlr.v4.runtime.misc.Pair;
import org.apache.tomcat.util.json.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    private CategoryService categoryService;
    @Autowired
    private UserService userService;
    @Autowired
    private JwtService jwtService;
    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private PhotoService photoService;
    @Autowired
    private GeoConversionService geoConversionService;

    @Operation(summary = "Get all reports")
    @GetMapping( "/public/report/getAll")
    public List<Report> getAllReports() {
        return reportService.getAllReports();
    }

    @Operation(summary = "Get a report by its id")
    @GetMapping( "/public/report/{reportId}")
    public ResponseEntity<Report> getReportById(@PathVariable("reportId") int reportId) {
        return reportService.getReportById(reportId);
    }

    @Operation(summary = "Get statistics")
    @GetMapping("/public/statistics")
    public Map<String, Integer> reportStatistics() {
        return reportService.getReportsStatistics();
    }

    @Operation(summary = "Anonymous user gets it's report")
    @GetMapping("/public/lookup/{businessId}")
    public ResponseEntity<Report> getReportByBusinessId(@PathVariable("businessId") UUID businessId) {
        return reportService.getReportByBusinessId(businessId);
    }

    @Operation(summary = "Create a report")
    @PostMapping("/public/report")
    @Transactional
    public ResponseEntity<?> createReport(@RequestBody CreateReportRequestDto reportRequest, HttpServletRequest httpRequest) throws JsonProcessingException {

        // Fill in the address
        if (reportRequest.getAddress() == null && reportRequest.getLatitude() != null && reportRequest.getLongitude() != null) {
            String address = geoConversionService.convertCoordinatesToAddress(reportRequest.getLatitude(), reportRequest.getLongitude());
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(address);
            address = jsonNode.get("display_name").asText();
            reportRequest.setAddress(address);
        } else if (reportRequest.getAddress() != null &&
                reportRequest.getLatitude() == null &&
                reportRequest.getLongitude() == null) {
            String location = geoConversionService.convertAddressToCoordinates(reportRequest.getAddress());
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode[] jsonNodes = objectMapper.readValue(location, JsonNode[].class);
            Double latitude = jsonNodes[0].get("lat").asDouble();
            Double longitude = jsonNodes[0].get("lon").asDouble();
            reportRequest.setLatitude(latitude);
            reportRequest.setLongitude(longitude);
        }

        Optional<Category> optionalCategory = categoryService.getCategoryById(reportRequest.getCategoryId());
        if (optionalCategory.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Kategorija nije pronadena");
        }
        Category category = optionalCategory.get();

        User user = null;
        String authorizationHeader = httpRequest.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            Integer userId = jwtService.extractUserId(token);
            Optional<User> optionalUser = userService.getUserById(userId);
            if (optionalUser.isEmpty()) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Korisnik tokena nije pronaden.");
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
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Nije moguce stvoriti problem objekt");
            }
        } else {
            Optional<Problem> optionalProblem = problemService.getProblemById(reportRequest.getMergeProblemId());
            if (optionalProblem.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Problem za merge nije pronaden");
            }
            savedProblem = optionalProblem.get();
        }

        // Create photos
        List<Photo> photos = new ArrayList<>();
        if (reportRequest.getBase64Photos() != null) {
            for (String base64Photo : reportRequest.getBase64Photos()) {
                Photo photo = Photo.builder()
                        .photoData(Base64.getDecoder().decode(base64Photo))
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

    @PostMapping("/public/nearbyReport")
    public ResponseEntity<?> getNearbyReport(@RequestBody CreateReportRequestDto reportRequest) {
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
            @RequestBody List<Report> reportList
    ) {
        return reportService.groupReports(problemId, reportList);
    }

    @Operation(summary = "Delete a report")
    @DeleteMapping("/advanced/report/{reportId}")
    public ResponseEntity<String> deleteReport(@PathVariable("reportId") int reportId) {
        return reportService.deleteReport(reportId);
    }

}
