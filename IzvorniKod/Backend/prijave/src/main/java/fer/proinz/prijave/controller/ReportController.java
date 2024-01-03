package fer.proinz.prijave.controller;

import fer.proinz.prijave.model.Report;
import fer.proinz.prijave.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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

    private final ReportService reportService;

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
    @Transactional
    public ResponseEntity<Report> createReport(@RequestBody Report report) {
        Report saved = reportService.createReport(report);
        entityManager.refresh(saved);
        return ResponseEntity.ok(saved);
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
