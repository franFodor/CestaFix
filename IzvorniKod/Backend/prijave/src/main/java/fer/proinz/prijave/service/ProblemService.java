package fer.proinz.prijave.service;

import fer.proinz.prijave.model.*;
import fer.proinz.prijave.repository.CityDeptRepository;
import fer.proinz.prijave.repository.ProblemRepository;
import fer.proinz.prijave.repository.ReportRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

    private final JwtService jwtService;

    private final UserService userService;

    public List<Problem> getAllProblems() {
        return problemRepository.findAll();
    }

    public Optional<Problem> getProblemById(int problemId) {
        return problemRepository.findById(problemId);
    }

    public Problem createProblem(Problem problem) {
        return problemRepository.save(problem);
    }

    public Problem updateProblem(int problemId, Problem updatedProblem) {
        return problemRepository.findById(problemId)
                .map(problem -> {
                    if (updatedProblem.getLongitude() != null) {
                        problem.setLongitude(updatedProblem.getLongitude());
                    }
                    if (updatedProblem.getLatitude() != null) {
                        problem.setLatitude(updatedProblem.getLatitude());
                    }
                    if (updatedProblem.getStatus() != null) {
                        Optional<Problem> problemOptional = problemRepository.findById(problemId);
                        if (problemOptional.isPresent()) {
                            Problem problem1 = problemOptional.get();
                            for (Report report : problem1.getReports()) {
                                report.setStatus(updatedProblem.getStatus());
                                reportRepository.save(report);
                            }
                        }
                        problem.setStatus(updatedProblem.getStatus());
                    }
                    return problemRepository.save(problem);
                })
                .orElseThrow(RuntimeException::new);
    }

    public ResponseEntity<String> deleteProblem(int problemId) {
        Optional<Problem> problemOptional = problemRepository.findById(problemId);
        if (problemOptional.isPresent()) {
            problemRepository.deleteById(problemId);
            return ResponseEntity.ok("Problem with id " + problemId + " is deleted.");
        } else {
            throw new RuntimeException("Problem with id " + problemId + " doesn't exists!");
        }
    }

    public List<Problem> getProblemsForCityDept(int cityDeptId) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        int userCityDeptId = user.getCityDept().getCityDeptId();

        Optional<CityDept> optionalCityDept = cityDeptRepository.findById(cityDeptId);
        if (optionalCityDept.isPresent()) {
            int categoryId = optionalCityDept.get().getCategory().getCategoryId();
            List<Problem> result = new ArrayList<>();
            if (cityDeptId == userCityDeptId) {
                for (Problem problem : getAllProblems()) {
                    if (problem.getCategory().getCategoryId() == categoryId) {
                        result.add(problem);
                    }
                }
            } else {
                throw new RuntimeException("Trying to access another city department!");
            }
            return result;
        } else {
            throw new RuntimeException("City Department doesn't exist!");
        }
    }
}
