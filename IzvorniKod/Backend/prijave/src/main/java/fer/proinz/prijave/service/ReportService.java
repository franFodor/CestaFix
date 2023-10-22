package fer.proinz.prijave.service;

import fer.proinz.prijave.model.Report;
import fer.proinz.prijave.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
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

    /**
     *
     * @param reportId
     * @return
     */
    public Optional<Report> getReportById(int reportId) {
        return reportRepository.findById(reportId);
    }

    public void addNewReport(Report report) {
        reportRepository.save(report);
    }

    public void deleteReport(int reportId) {
        Optional<Report> reportOptional = reportRepository.findById(reportId);

        if(reportOptional.isPresent()) {
            reportRepository.deleteById(reportId);
        } else {
            throw new RuntimeException("report with id " + reportId + " does not exists!");
        }
    }

    public Report updateReport(int reportId,Report report) {
        report.setReportId(reportId);
        return reportRepository.save(report);
    }

}
