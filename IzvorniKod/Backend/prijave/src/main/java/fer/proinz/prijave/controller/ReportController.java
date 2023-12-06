package fer.proinz.prijave.controller;

import fer.proinz.prijave.model.Report;
import fer.proinz.prijave.service.ReportService;
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

    @GetMapping( "/public/report/getAll")
    public List<Report> getAllReports() {
        return reportService.getAllReports();
    }

    @GetMapping( "/public/report/{reportId}")
    public ResponseEntity<Report> getReportById(@PathVariable("reportId") int reportId) {
        Optional<Report> reportOptional = reportService.getReportById(reportId);
        return reportOptional.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());

    }

    @PostMapping("/public/report")
    public ResponseEntity<Report> createReport(@RequestBody Report report) {
        Report saved = reportService.createReport(report);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/advanced/report/{reportId}")
    public ResponseEntity<Report> updateReport(
            @PathVariable("reportId") int reportId,
            @RequestBody Report updatedReport
        ) {
        return ResponseEntity.ok(reportService.updateReport(reportId, updatedReport));
    }

    @DeleteMapping("/advanced/report/{reportId}")
    public ResponseEntity<String> deleteReport(@PathVariable("reportId") int reportId) {
        return reportService.deleteReport(reportId);
    }

}
