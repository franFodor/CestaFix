package fer.proinz.prijave.controller;

import fer.proinz.prijave.model.Report;
import fer.proinz.prijave.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/report")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping( "/getAll")
    public ResponseEntity<List<Report>> getAllReports() {
        List<Report> reportList = reportService.getAllReports();
        return ResponseEntity.ok(reportList);
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

    @PostMapping()
    public ResponseEntity<String> createNewReport(@RequestBody Report report) {
        reportService.addNewReport(report);
        return ResponseEntity.ok("Prijavili ste prijavu!");
    }

    @PutMapping(path = "/{reportId}")
    public ResponseEntity<String> updateReport(
            @PathVariable("reportId") int reportId, @RequestBody Report report)  {
        return null;
    }

    @DeleteMapping(path = "/{reportId}")
    public ResponseEntity<String> deleteReport(@PathVariable("reportId") int reportId) {
        reportService.deleteReport(reportId);
        return ResponseEntity.ok("Izbrisali ste prijavu!");
    }

}
