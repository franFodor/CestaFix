package fer.proinz.prijave.service;

import fer.proinz.prijave.model.Problem;
import fer.proinz.prijave.model.Report;
import fer.proinz.prijave.repository.ProblemRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProblemService {

    private final ProblemRepository problemRepository;

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
        /*Optional<Problem> problemOptional = problemRepository.findById(problemId);
        if (problemOptional.isPresent()) {
            Problem saved = problemRepository.save(updatedProblem);
            return saved;
        } else {
            throw new NoSuchElementException("No problem with this id");
        }*/
        return problemRepository.findById(problemId)
                .map(problem -> {
                    if (updatedProblem.getLongitude() != null) {
                        problem.setLongitude(updatedProblem.getLongitude());
                    }
                    if (updatedProblem.getLatitude() != null) {
                        problem.setLatitude(updatedProblem.getLatitude());
                    }
                    if (updatedProblem.getStatus() != null) {
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
            throw new RuntimeException("Problem with id " + problemId + " does not exists!");
        }
    }

    public List<Problem> getProblemsByCategoryId(int categoryId) {
        return problemRepository.findByCategory_CategoryId(categoryId);
    }
}
