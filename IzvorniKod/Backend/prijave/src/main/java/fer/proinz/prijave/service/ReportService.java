package fer.proinz.prijave.service;

import fer.proinz.prijave.model.Report;
import fer.proinz.prijave.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;

    public List<Report> getAllReports() {
        return reportRepository.findAll();
    }

    public Optional<Report> getReportById(int reportId) {
        return reportRepository.findById(reportId);
    }

    public Report createReport(Report report) {
        return reportRepository.save(report);
    }

    public Report updateReport(int reportId, Report updatedReport) {
        Optional<Report> report = reportRepository.findById(reportId);
        if (report.isPresent()) {
            Report saved = reportRepository.save(updatedReport);
            return saved;
        } else {
            throw new NoSuchElementException("No report with this id");
        }
    }

    public ResponseEntity<String> deleteReport(int reportId) {
        Optional<Report> reportOptional = reportRepository.findById(reportId);

        if(reportOptional.isPresent()) {
            reportRepository.deleteById(reportId);
            return ResponseEntity.ok("Report with id " + reportId + " is deleted.");
        } else {
            throw new RuntimeException("Report with id " + reportId + " does not exists!");
        }
    }

}
