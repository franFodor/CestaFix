package fer.proinz.prijave.service;

import fer.proinz.prijave.model.Problem;
import fer.proinz.prijave.repository.ProblemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
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
}
