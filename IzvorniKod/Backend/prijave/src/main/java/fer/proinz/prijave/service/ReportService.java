package fer.proinz.prijave.service;

import fer.proinz.prijave.model.Problem;
import fer.proinz.prijave.model.Report;
import fer.proinz.prijave.model.Role;
import fer.proinz.prijave.model.User;
import fer.proinz.prijave.repository.ProblemRepository;
import fer.proinz.prijave.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;

    private final ProblemRepository problemRepository;

    private final ProblemService problemService;

    public List<Report> getAllReports() {
        return reportRepository.findAll();
    }

    public Optional<Report> getReportById(int reportId) {
        return reportRepository.findById(reportId);
    }

    public Report createReport(Report report) {
        return reportRepository.save(report);
    }

    public ResponseEntity<?> updateReport(int reportId, Report updatedReport) {
        Optional<Report> reportOptional = reportRepository.findById(reportId);
        if (reportOptional.isPresent()) {
            Report report = reportOptional.get();
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User user = (User) authentication.getPrincipal();

            if (report.getUser() == null && user.getRole() != Role.STAFF) {
                return ResponseEntity.badRequest().body("The report trying to be updated is anonymous");
            }
            if (user.getRole() == Role.USER && report.getUser().getUserId() != user.getUserId()) {
                return ResponseEntity.badRequest().body("User tried to update a report that they didn't make");
            } else {
                reportRepository.findById(reportId)
                        .map(rep -> {
                            if (updatedReport.getTitle() != null) {
                                rep.setTitle(updatedReport.getTitle());
                            }
                            if (updatedReport.getDescription() != null) {
                                rep.setDescription(updatedReport.getDescription());
                            }
                            if (updatedReport.getAddress() != null) {
                                rep.setAddress(updatedReport.getAddress());
                            }
                            if (updatedReport.getPhoto() != null) {
                                rep.setPhoto(updatedReport.getPhoto());
                            }
                            if (updatedReport.getStatus() != null) {
                                rep.setStatus(updatedReport.getStatus());
                            }
                            return reportRepository.save(rep);
                        })
                        .orElseThrow(RuntimeException::new);
                return ResponseEntity.ok(reportRepository.findById(reportId));
            }
        } else {
            return ResponseEntity.badRequest().body("The report that you wanted to update doesn't exist");
        }
    }

    public ResponseEntity<String> deleteReport(int reportId) {
        Optional<Report> reportOptional = reportRepository.findById(reportId);
        if (reportOptional.isPresent()) {
            Report report = reportOptional.get();
            Problem problem = report.getProblem();
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User user = (User) authentication.getPrincipal();

            if (report.getUser() == null && user.getRole() != Role.STAFF) {
                return ResponseEntity.badRequest().body("The report trying to be deleted is anonymous");
            }
            if (user.getRole() == Role.USER && report.getUser().getUserId() != user.getUserId()) {
                return ResponseEntity.badRequest().body("User tried to delete a report that they didn't make");
            } else {
                if (problem != null) {
                    problem.getReports().remove(report);
                    report.setProblem(null);
                    problemService.updateProblem(problem.getProblemId(), problem);
                    reportRepository.save(report);

                    if (problem.getReports().isEmpty()) {
                        problemService.deleteProblem(problem.getProblemId());
                    }
                }
            }

            reportRepository.deleteById(reportId);
            return ResponseEntity.ok("Report with id " + reportId + " is deleted.");
        } else {
            throw new RuntimeException("Report with id " + reportId + " does not exists!");
        }
    }

}
