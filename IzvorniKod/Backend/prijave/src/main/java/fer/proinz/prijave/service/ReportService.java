package fer.proinz.prijave.service;

import fer.proinz.prijave.dto.CreateReportRequestDto;
import fer.proinz.prijave.model.*;
import fer.proinz.prijave.repository.ProblemRepository;
import fer.proinz.prijave.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;

    private final ProblemRepository problemRepository;

    private final ProblemService problemService;

    public List<Report> getAllReports() {
        List<Report> reports = reportRepository.findAll();
        for (Report report : reports) {
            // convert photos from byte[] to base64 String
            getReportById(report.getReportId());
        }
        return reports;
    }

    public ResponseEntity<Report> getReportById(int reportId) {
        Optional<Report> reportOptional = reportRepository.findById(reportId);
        if (reportOptional.isPresent()) {
            Report report = reportOptional.get();
            List<String> base64Photos = new ArrayList<>();

            if (report.getPhotos() != null) {
                for (Photo photo : report.getPhotos()) {
                    base64Photos.add(Base64.encodeBase64String(photo.getPhotoData()));
                }
                report.setBase64Photos(base64Photos);
                reportRepository.save(report);
            } else {
                report.setBase64Photos(null);
                reportRepository.save(report);
            }

            return ResponseEntity.ok(report);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    public ResponseEntity<Report> getReportByBusinessId(UUID businessId) {
        Optional<Report> reportOptional = reportRepository.findByBusinessId(businessId);
        if (reportOptional.isPresent()) {
            Report report = reportOptional.get();
            return ResponseEntity.ok(report);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    public Map<String, Integer> getReportsStatistics() {
        Map<String, Integer> statistics = new HashMap<>();

        statistics.put("Broj prijava koje cekaju obradu", reportRepository.countByStatus("Ceka obradu"));
        statistics.put("Broj prijava koje su u obradi", reportRepository.countByStatus("U obradi"));
        statistics.put("Broj prijava koje su zavrsene", reportRepository.countByStatus("Zavrseno"));

        return statistics;
    }

    public Report createReport(Report report) {
        

        return reportRepository.save(report);
    }

    // function to calculate the distance of nearbyReport
    public double haversineDistance(CreateReportRequestDto reportRequest, Report report) {
        double dLat = (reportRequest.getLatitude() - report.getLatitude()) * Math.PI / 180;
        double dLon = (reportRequest.getLongitude() - report.getLongitude()) * Math.PI / 180;
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(reportRequest.getLatitude()) * Math.cos(report.getLatitude()) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return 6371e3 * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }

    public Integer getNearbyReport(CreateReportRequestDto reportRequest) {
        List<Report> reportList = getAllReports();
        for (Report report : reportList) {
            double distance = haversineDistance(reportRequest, report);
            if (distance < 100 &&
                    Math.abs(report.getReportTime().getTime() - System.currentTimeMillis()) < 604800000 &&
                    report.getProblem().getCategory().getCategoryId() == reportRequest.getCategoryId()) {
                return report.getProblem().getProblemId();
            }
        }
        return null;
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
                            if (updatedReport.getPhotos() != null) {
                                rep.setPhotos(updatedReport.getPhotos());
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

    public ResponseEntity<?> groupReports(int problemId, List<Integer> reportIds) {
        Optional<Problem> problemOptional = problemRepository.findById(problemId);
        if (problemOptional.isPresent()) {
            Problem problemToGroupTo = problemOptional.get();
            for (Integer reportId : reportIds) {
                Optional<Report> reportOptional = reportRepository.findById(reportId);
                if (reportOptional.isPresent()) {
                    Report report = reportOptional.get();
                    Optional<Problem> reportProblemOptional = problemRepository.findById(report.getProblem().getProblemId());
                    if (reportProblemOptional.isPresent()) {
                        Problem reportProblem = reportProblemOptional.get();
                        reportProblem.getReports().remove(report);
                        report.setProblem(problemToGroupTo);
                        problemService.updateProblem(reportProblem.getProblemId(), reportProblem);
                        reportRepository.save(report);

                        if (reportProblem.getReports().isEmpty()) {
                            problemService.deleteProblem(reportProblem.getProblemId());
                        }
                    }
                }
            }
            return ResponseEntity.ok(problemRepository.findById(problemId));
        } else {
            return ResponseEntity.internalServerError().body("Report grouping didn't work");
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
