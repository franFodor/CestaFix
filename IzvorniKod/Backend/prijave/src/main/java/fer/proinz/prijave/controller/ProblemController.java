package fer.proinz.prijave.controller;

import fer.proinz.prijave.model.Problem;
import fer.proinz.prijave.service.ProblemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/problems")
public class ProblemController {

    @Autowired
    private ProblemService problemService;

    @GetMapping("/getAllProblems")
    public List<Problem> getAllProblems() {
        return problemService.getAllProblems();
    }

    @GetMapping("/get/{problemId}")
    public ResponseEntity<Problem> getProblemById(@PathVariable("problemId") int problemId) {
        Optional<Problem> problemOptional = problemService.getProblemById(problemId);
        if(problemOptional.isPresent()) {
            return ResponseEntity.ok(problemOptional.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
