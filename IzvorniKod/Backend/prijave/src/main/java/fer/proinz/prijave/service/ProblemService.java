package fer.proinz.prijave.service;

import fer.proinz.prijave.model.CityDeptCategory;
import fer.proinz.prijave.model.Problem;
import fer.proinz.prijave.model.Report;
import fer.proinz.prijave.model.User;
import fer.proinz.prijave.repository.CityDeptCategoryRepository;
import fer.proinz.prijave.repository.ProblemRepository;
import fer.proinz.prijave.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProblemService {

    private final ProblemRepository problemRepository;

    private final CityDeptCategoryRepository cityDeptCategoryRepository;

    private final ReportRepository reportRepository;

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

    public List<Problem> getProblemsByCategoryId(int categoryId) {
        return problemRepository.findByCategory_CategoryId(categoryId);
    }

    public List<Problem> getProblemsForUser(User user) {
        // Fetch CityDeptCategory instances related to the user's CityDepartment
        List<CityDeptCategory> cityDeptCategories = cityDeptCategoryRepository.findByCityDepartment(user.getCitydept());

        // Extract Category IDs from CityDeptCategory instances
        List<Integer> categoryIds = cityDeptCategories.stream()
                .map(cityDeptCategory -> cityDeptCategory.getCategory().getCategoryId())
                .collect(Collectors.toList());

        // Fetch problems for each category ID and flatten the result

        List<Problem> userProblems = categoryIds.stream()
                .flatMap(categoryId -> problemRepository.findByCategory_CategoryId(categoryId).stream())
                .distinct()
                .collect(Collectors.toList());

        return userProblems;
    }
}
