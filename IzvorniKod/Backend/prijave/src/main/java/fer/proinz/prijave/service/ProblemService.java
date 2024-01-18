package fer.proinz.prijave.service;

import fer.proinz.prijave.exception.NonExistingCityDeptException;
import fer.proinz.prijave.exception.NonExistingProblemException;
import fer.proinz.prijave.exception.NonExistingReportException;
import fer.proinz.prijave.model.*;
import fer.proinz.prijave.repository.CityDeptRepository;
import fer.proinz.prijave.repository.ProblemRepository;
import fer.proinz.prijave.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProblemService {

    private final ProblemRepository problemRepository;

    private final ReportRepository reportRepository;

    private final CityDeptRepository cityDeptRepository;

    public List<Problem> getAllProblems() {
        return problemRepository.findAll();
    }

    public Optional<Problem> getProblemById(int problemId) {
        return problemRepository.findById(problemId);
    }

    public List<Problem> getProblemsForCityDept(int cityDeptId) throws NonExistingCityDeptException {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        CityDept cityDept = cityDeptRepository
                .findById(cityDeptId)
                .orElseThrow(NonExistingCityDeptException::new);

        List<Problem> result = new ArrayList<>();
        if (cityDeptId == user.getCityDept().getCityDeptId()) {
            for (Problem problem : getAllProblems()) {
                if (problem.getCategory().getCategoryId() == cityDept.getCategory().getCategoryId()) {
                    result.add(problem);
                }
            }
        } else {
            throw new RuntimeException("Trying to access another city department!");
        }
        return result;
    }

    public Problem createProblem(Problem problem) {
        return problemRepository.save(problem);
    }

    public Problem updateProblem(int problemId, Problem updatedProblem) throws NonExistingProblemException {
        return problemRepository.findById(problemId)
                .map(problem -> {
                    if (updatedProblem.getStatus() != null) {
                        Problem problem1 = null;
                        try {
                            problem1 = problemRepository
                                    .findById(problemId)
                                    .orElseThrow(NonExistingProblemException::new);
                        } catch (NonExistingProblemException e) {
                            throw new RuntimeException(e);
                        }
                        for (Report report : problem1.getReports()) {
                            // Update the status of all reports in the problem
                            report.setStatus(updatedProblem.getStatus());
                            reportRepository.save(report);
                        }
                        problem.setStatus(updatedProblem.getStatus());
                    }
                    return problemRepository.save(problem);
                })
                .orElseThrow(NonExistingProblemException::new);
    }

    public Problem groupReports(int problemId, List<Integer> reportIds)
            throws NonExistingProblemException, NonExistingReportException {
        Problem problemToGroupTo = problemRepository
                .findById(problemId)
                .orElseThrow(NonExistingProblemException::new);

        for (Integer reportId : reportIds) {
            Report report = reportRepository
                    .findById(reportId)
                    .orElseThrow(NonExistingReportException::new);

            Problem reportProblem = problemRepository
                    .findById(report.getProblem().getProblemId())
                    .orElseThrow(NonExistingProblemException::new);

            reportProblem.getReports().remove(report);
            report.setProblem(problemToGroupTo);
            //problemService.updateProblem(reportProblem.getProblemId(), reportProblem);
            problemRepository.save(reportProblem);
            reportRepository.save(report);

            if (reportProblem.getReports().isEmpty()) {
                deleteProblem(reportProblem.getProblemId());
            }
        }
        return problemToGroupTo;
    }

    public ResponseEntity<String> deleteProblem(int problemId) throws NonExistingProblemException {
        problemRepository.findById(problemId)
                .orElseThrow(NonExistingProblemException::new);

        problemRepository.deleteById(problemId);
        return ResponseEntity.ok("Problem with id " + problemId + " is deleted.");
    }
}
