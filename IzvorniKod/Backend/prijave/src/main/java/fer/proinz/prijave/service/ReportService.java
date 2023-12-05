package fer.proinz.prijave.service;

import fer.proinz.prijave.model.Report;
import fer.proinz.prijave.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
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
        return reportRepository.findById(reportId)
                .map(report -> {
                    if (updatedReport.getTitle() != null) {
                        report.setTitle(updatedReport.getTitle());
                    }
                    if (updatedReport.getDescription() != null) {
                        report.setDescription(updatedReport.getDescription());
                    }
                    return reportRepository.save(report);
                })
                .orElseThrow(RuntimeException::new);
    }

    public ResponseEntity<Report> deleteReport(int reportId) {
        Optional<Report> reportOptional = reportRepository.findById(reportId);

        if(reportOptional.isPresent()) {
            reportRepository.deleteById(reportId);
            return ResponseEntity.ok(reportOptional.get());
        } else {
            throw new RuntimeException("report with id " + reportId + " does not exists!");
        }
    }

}
