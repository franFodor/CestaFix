package fer.proinz.prijave.controller;

import fer.proinz.prijave.model.Problem;
import fer.proinz.prijave.service.ProblemService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class ProblemController {

    private final ProblemService problemService;

    @GetMapping("/public/problems/getAll")
    public List<Problem> getAllProblems() {
        return problemService.getAllProblems();
    }

    @GetMapping("/public/problems/{problemId}")
    public ResponseEntity<Problem> getProblemById(@PathVariable("problemId") int problemId) {
        Optional<Problem> problemOptional = problemService.getProblemById(problemId);
        return problemOptional.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
