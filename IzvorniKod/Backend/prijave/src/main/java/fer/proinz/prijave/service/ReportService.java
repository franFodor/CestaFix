package fer.proinz.prijave.service;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.GpsDirectory;
import fer.proinz.prijave.dto.ReportRequestDto;
import fer.proinz.prijave.exception.NonExistingCategoryException;
import fer.proinz.prijave.exception.NonExistingProblemException;
import fer.proinz.prijave.exception.NonExistingReportException;
import fer.proinz.prijave.exception.NotEnoughDataException;
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

    private final PhotoService photoService;

    private final EntityManager entityManager;

    public List<Report> getAllReports() {
        return reportRepository.findAll();
    }

    public Optional<Report> getReportById(int reportId) {
        return reportRepository.findById(reportId);
    }

    public Report getReportByBusinessId(UUID businessId) throws NonExistingReportException {
        return reportRepository
                .findByBusinessId(businessId)
                .orElseThrow(NonExistingReportException::new);
    }

    public Map<String, Integer> getReportsStatistics() {
        Map<String, Integer> statistics = new HashMap<>();

        statistics.put("Broj prijava koje cekaju obradu", reportRepository.countByStatus("Čeka Obradu"));
        statistics.put("Broj prijava koje su u obradi", reportRepository.countByStatus("U obradi"));
        statistics.put("Broj prijava koje su obrađene", reportRepository.countByStatus("Obrađeno"));

        return statistics;
    }

    public ReportRequestDto validateLocation(ReportRequestDto reportRequest)
            throws IOException, ImageProcessingException, NotEnoughDataException {

        if (reportRequest.getAddress() != null && reportRequest.hasCoordinates()) {
            // If reportRequest has address and coordinates
            return reportRequest;

        }else if (reportRequest.needsToConvertCoordinatesToAddress()) {

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
                    // Set latitude and longitude
                    reportRequest.setGeo(gpsDir.getGeoLocation());
                    reportRequest = geoConversionService.convertCoordinatesToAddress(reportRequest);
                    break;  // Found photo with coordinates
                } else {
                    return null;
                }
            }
        } else {
            throw new NotEnoughDataException();
        }
        return reportRequest;
    }

    public Report createReport(ReportRequestDto reportRequest)
            throws IOException, ImageProcessingException, NonExistingCategoryException,
            NonExistingProblemException, NotEnoughDataException {
        reportRequest = validateLocation(reportRequest);
        if (reportRequest == null) {
            throw new NotEnoughDataException();
        }

        // Find the Category object or throw an exception
        Category category = categoryService
                .getCategoryById(reportRequest.getCategoryId())
                .orElseThrow(NonExistingCategoryException::new);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user;
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            user = (User) authentication.getPrincipal();    // User is logged in
        } else {
            user = null;        // User is NOT logged in (anonymous)
        }

        Problem savedProblem = null;
        if (reportRequest.getMergeProblemId() == null) {
            // Build new Problem object for Report
            Problem problem = Problem.builder()
                    .latitude(reportRequest.getLatitude())
                    .longitude(reportRequest.getLongitude())
                    .status(reportRequest.getProblemStatus())
                    .category(category)
                    .build();

            // Save the Problem object
            savedProblem = problemService.createProblem(problem);
        } else {
            // Find the Problem object or throw an exception
            savedProblem = problemService
                    .getProblemById(reportRequest.getMergeProblemId())
                    .orElseThrow(NonExistingProblemException::new);
        }

        // Convert and store photos
        List<Photo> photos = new ArrayList<>();
        if (reportRequest.getBase64Photos() != null) {
            for (String base64Photo : reportRequest.getBase64Photos()) {
                // Build and save a Photo object for every base64String
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

        // Go through all the photos and set the report attribute
        for (Photo photo : photos) {
            photo.setReport(savedReport);
            photoService.createPhoto(photo);
        }
        entityManager.refresh(savedReport);
        savedReport.setPhotos(photos);
        reportRepository.save(savedReport);

        return savedReport;
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

    public Integer getNearbyReport(ReportRequestDto reportRequest)
            throws IOException, ImageProcessingException, NotEnoughDataException {
        if (!reportRequest.hasCoordinates()) {
            reportRequest = validateLocation(reportRequest);
        }
        if (reportRequest == null) {
            throw new NotEnoughDataException();
        }

        for (Report report : getAllReports()) {
            double distance = haversineDistance(reportRequest, report);
            if (distance < 100 &&
                    Math.abs(report.getReportTime().getTime() - System.currentTimeMillis()) < 604800000 &&
                    report.getProblem().getCategory().getCategoryId() == reportRequest.getCategoryId()) {
                return report.getProblem().getProblemId();
            }
        }
        return -1;
    }

    public ResponseEntity<?> updateReport(int reportId, Report updatedReport)
            throws NonExistingReportException {
        Report report = reportRepository
                .findById(reportId)
                .orElseThrow(NonExistingReportException::new);
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (user.getRole() != Role.STAFF && report.getUser() == null) {
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
    }

    public ResponseEntity<String> deleteReport(int reportId)
            throws NonExistingReportException, NonExistingProblemException {
        Report report = reportRepository
                .findById(reportId)
                .orElseThrow(NonExistingReportException::new);
        Problem problem = report.getProblem();
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (user.getRole() != Role.STAFF && report.getUser() == null) {
            return ResponseEntity.badRequest().body("The report trying to be deleted is anonymous");
        } else if (user.getRole() == Role.USER && report.getUser().getUserId() != user.getUserId()) {
            return ResponseEntity.badRequest().body("User tried to delete a report that they didn't make");
        } else {
            problem.getReports().remove(report);
            report.setProblem(null);
            //problemService.updateProblem(problem.getProblemId(), problem);
            problemRepository.save(problem);
            reportRepository.save(report);

            if (problem.getReports().isEmpty()) {
                problemService.deleteProblem(problem.getProblemId());
            }
        }

        reportRepository.deleteById(reportId);
        return ResponseEntity.ok("Report with id " + reportId + " is deleted.");
    }

}
