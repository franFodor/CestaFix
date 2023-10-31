package fer.proinz.prijave.controller;

import fer.proinz.prijave.model.Report;
import fer.proinz.prijave.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/report")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping( "/getAllReports")
    public List<Report> getAllReports() {
        return reportService.getAllReports();
    }

    @GetMapping( "/get/{reportId}")
    public ResponseEntity<Report> getReportById(@PathVariable("reportId") int reportId) {
        Optional<Report> reportOptional = reportService.getReportById(reportId);
        if(reportOptional.isPresent()) {
            return ResponseEntity.ok(reportOptional.get());
        } else {
            return ResponseEntity.notFound().build();
        }

    }

    @PostMapping("")
    public ResponseEntity<Report> createReport(@RequestBody Report report) {
        Report saved = reportService.createReport(report);
        return ResponseEntity
                .created(URI.create("/report/" + saved.getReportId()))
                .body(saved);
    }

    @Secured("ADMIN")
    @PutMapping("/{reportId}")
    public Report updateReport(
            @PathVariable("reportId") int reportId,
            @RequestBody Report updatedReport
        ) {
        return reportService.updateReport(reportId, updatedReport);
    }

    @Secured("ADMIN")
    @DeleteMapping("/{reportId}")
    public Optional<Report> deleteReport(@PathVariable("reportId") int reportId) {
        return reportService.deleteReport(reportId);
    }

}
