package fer.proinz.prijave.controller;

import fer.proinz.prijave.dto.CreateReportRequestDto;
import fer.proinz.prijave.model.Category;
import fer.proinz.prijave.model.Problem;
import fer.proinz.prijave.model.Report;
import fer.proinz.prijave.service.CategoryService;
import fer.proinz.prijave.service.ProblemService;
import fer.proinz.prijave.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;

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

    @PersistenceContext
    private EntityManager entityManager;

    @Operation(summary = "Get all reports")
    @GetMapping( "/public/report/getAll")
    public List<Report> getAllReports() {
        return reportService.getAllReports();
    }

    @Operation(summary = "Get a report by its id")
    @GetMapping( "/public/report/{reportId}")
    public ResponseEntity<Report> getReportById(@PathVariable("reportId") int reportId) {
        Optional<Report> reportOptional = reportService.getReportById(reportId);
        return reportOptional.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());

    }

    @Operation(summary = "Create a report")
    @PostMapping("/public/report")
    public ResponseEntity<?> createReport(@RequestBody CreateReportRequestDto reportRequest) {
        // Create or find the Category
        Optional<Category> optionalCategory = categoryService.getCategoryById(reportRequest.getCategoryId());
        if (!optionalCategory.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Kategorija nije pronadena");
        }
        Category category = optionalCategory.get();

        // Create new Problem
        Problem problem = Problem.builder()
                .longitude(reportRequest.getProblemLongitude())
                .latitude(reportRequest.getProblemLatitude())
                .status(reportRequest.getProblemStatus())
                .category(category)
                .build();

        // Save the Problem object
        Problem savedProblem = problemService.createProblem(problem);
        if (savedProblem == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Nije moguce stvoriti problem objekt");
        }

        // Create new Report
        Report report = Report.builder()
                .title(reportRequest.getTitle())
                .description(reportRequest.getDescription())
                .address(reportRequest.getAddress())
                .photo(reportRequest.getPhoto())
                .status(reportRequest.getReportStatus())
                .longitude(reportRequest.getProblemLongitude())
                .latitude(reportRequest.getProblemLatitude())
                .problem(savedProblem)
                .build();

        // Save the Report object
        Report savedReport = reportService.createReport(report);
        return ResponseEntity.ok(savedReport);
    }

    @Operation(summary = "Update a report")
    @PatchMapping("/advanced/report/{reportId}")
    public ResponseEntity<Report> updateReport(
            @PathVariable("reportId") int reportId,
            @RequestBody Report updatedReport
        ) {
        return ResponseEntity.ok(reportService.updateReport(reportId, updatedReport));
    }

    @Operation(summary = "Delete a report")
    @DeleteMapping("/advanced/report/{reportId}")
    public ResponseEntity<String> deleteReport(@PathVariable("reportId") int reportId) {
        return reportService.deleteReport(reportId);
    }

}
