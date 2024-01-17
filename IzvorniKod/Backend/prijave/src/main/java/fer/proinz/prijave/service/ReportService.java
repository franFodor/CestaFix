package fer.proinz.prijave.service;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.GpsDirectory;
import com.fasterxml.jackson.databind.ObjectMapper;
import fer.proinz.prijave.dto.ReportRequestDto;
import fer.proinz.prijave.exception.NonExistingCategoryException;
import fer.proinz.prijave.model.*;
import fer.proinz.prijave.repository.ProblemRepository;
import fer.proinz.prijave.repository.ReportRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;

    private final ProblemRepository problemRepository;

    private final ProblemService problemService;

    private final GeoConversionService geoConversionService;

    private final CategoryService categoryService;

    private final JwtService jwtService;

    private final UserService userService;

    private final PhotoService photoService;

    private final EntityManager entityManager;

    private final ObjectMapper objectMapper;

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

    public ReportRequestDto validateLocation(ReportRequestDto reportRequest)
            throws IOException, ImageProcessingException {
        if (reportRequest.getAddress() != null && reportRequest.hasCoordinates()) {
            return reportRequest;
        }else if (reportRequest.needsToConvertCoordinatesToAddress()) {

            //reportRequest = getAddressFromCoordinates(reportRequest);
            reportRequest = geoConversionService.convertCoordinatesToAddress(reportRequest);

        } else if (reportRequest.needsToConvertAddressToCoordinates()) {

            reportRequest = geoConversionService.convertAddressToCoordinates(reportRequest);

        } else if (reportRequest.needsToGetLocationFromPhoto()) {

            if (reportRequest.getBase64Photos().isEmpty()) {
                return null;
            }

            for (String base64Photo : reportRequest.getBase64Photos()) {
                // Extract EXIF metadata
                Metadata metadata = ImageMetadataReader
                        .readMetadata(new ByteArrayInputStream(Base64.decodeBase64(base64Photo)));

                // Get the GPS directory from the metadata
                GpsDirectory gpsDir = metadata.getFirstDirectoryOfType(GpsDirectory.class);

                if (gpsDir != null) {
                    // Extract latitude and longitude
                    reportRequest.setGeo(gpsDir.getGeoLocation());
                    reportRequest = geoConversionService.convertCoordinatesToAddress(reportRequest);
                    break;  // Found photo with coordinates
                } else {
                    return null;
                }
            }
        }
        return reportRequest;
    }

    public ResponseEntity<?> createReport(ReportRequestDto reportRequest)
            throws IOException, ImageProcessingException, NonExistingCategoryException {
        reportRequest = validateLocation(reportRequest);
        if (reportRequest == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No address, photo or coordinates given.");
        }

        Optional<Category> optionalCategory = categoryService.getCategoryById(reportRequest.getCategoryId());
        Category category = optionalCategory.orElseThrow(NonExistingCategoryException::new);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user;
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            user = (User) authentication.getPrincipal();
        } else {
            user = null;
        }

        Problem savedProblem = null;
        if (reportRequest.getMergeProblemId() == null) {
            Problem problem = Problem.builder()
                    .longitude(reportRequest.getLongitude())
                    .latitude(reportRequest.getLatitude())
                    .status(reportRequest.getProblemStatus())
                    .category(category)
                    .build();

            // Save the Problem object
            savedProblem = problemService.createProblem(problem);
            if (savedProblem == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Problem object cannot be initialized");
            }
        } else {
            Optional<Problem> optionalProblem = problemService.getProblemById(reportRequest.getMergeProblemId());
            if (optionalProblem.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nearby problem couldn't be found");
            }
            savedProblem = optionalProblem.get();
        }

        // Create photos
        List<Photo> photos = new ArrayList<>();
        if (reportRequest.getBase64Photos() != null) {
            for (String base64Photo : reportRequest.getBase64Photos()) {
                Photo photo = Photo.builder()
                        .photoData(Base64.decodeBase64(base64Photo))
                        .report(null)
                        .build();
                photoService.createPhoto(photo);
                photos.add(photo);
            }
        }

        // Build a new Report
        Report report = Report.builder()
                .user(user)
                .title(reportRequest.getTitle())
                .description(reportRequest.getDescription())
                .address(reportRequest.getAddress())
                .base64Photos(reportRequest.getBase64Photos())
                .status(reportRequest.getReportStatus())
                .latitude(reportRequest.getLatitude())
                .longitude(reportRequest.getLongitude())
                .problem(savedProblem)
                .build();

        Report savedReport = reportRepository.save(report);

        for (Photo photo : photos) {
            photo.setReport(savedReport);
            photoService.updatePhoto(photo.getPhotoId(), photo);
        }
        entityManager.refresh(savedReport);
        savedReport.setPhotos(photos);
        reportRepository.save(savedReport);

        return ResponseEntity.ok(savedReport);

    }

    // function to calculate the distance of nearbyReport
    public double haversineDistance(ReportRequestDto reportRequest, Report report) {
        double dLat = (reportRequest.getLatitude() - report.getLatitude()) * Math.PI / 180;
        double dLon = (reportRequest.getLongitude() - report.getLongitude()) * Math.PI / 180;
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(reportRequest.getLatitude()) * Math.cos(report.getLatitude()) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return 6371e3 * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }

    public Integer getNearbyReport(ReportRequestDto reportRequest) throws IOException, ImageProcessingException {
        if (!reportRequest.hasCoordinates()) {
            reportRequest = validateLocation(reportRequest);
        }
        if (reportRequest == null) {
            return null;
        }
        List<Report> reportList = getAllReports();
        for (Report report : reportList) {
            double distance = haversineDistance(reportRequest, report);
            if (distance < 100 &&
                    Math.abs(report.getReportTime().getTime() - System.currentTimeMillis()) < 604800000 &&
                    report.getProblem().getCategory().getCategoryId() == reportRequest.getCategoryId()) {
                return report.getProblem().getProblemId();
            }
        }
        return -1;
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
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

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
