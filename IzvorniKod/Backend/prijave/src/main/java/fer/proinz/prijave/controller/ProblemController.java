package fer.proinz.prijave.controller;

import fer.proinz.prijave.model.Problem;
import fer.proinz.prijave.service.ProblemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class ProblemController {

    private final ProblemService problemService;

    @GetMapping("/public/problem/getAll")
    public List<Problem> getAllProblems() {
        return problemService.getAllProblems();
    }

    @GetMapping("/public/problem/{problemId}")
    public ResponseEntity<Problem> getProblemById(@PathVariable("problemId") int problemId) {
        Optional<Problem> problemOptional = problemService.getProblemById(problemId);
        return problemOptional.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/public/problem")
    public ResponseEntity<Problem> createProblem(@RequestBody Problem problem) {
        Problem saved = problemService.createProblem(problem);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/advanced/problem/{problemId}")
    public ResponseEntity<Problem> updateReport(
            @PathVariable("problemId") int problemId,
            @RequestBody Problem updatedProblem
    ) {
        return ResponseEntity.ok(problemService.updateProblem(problemId, updatedProblem));
    }

    @DeleteMapping("/advanced/problem/{problemId}")
    public ResponseEntity<String> deleteProblem(@PathVariable("problemId") int problemId) {
        return problemService.deleteProblem(problemId);
    }
}
