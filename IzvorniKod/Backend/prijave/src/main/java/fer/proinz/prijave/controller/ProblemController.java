package fer.proinz.prijave.controller;

import fer.proinz.prijave.model.CitydepCategory;
import fer.proinz.prijave.model.Problem;
import fer.proinz.prijave.model.User;
import fer.proinz.prijave.service.ProblemService;
import fer.proinz.prijave.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class ProblemController {

    private final ProblemService problemService;
    private final UserService userService;

    @Operation(summary = "Get all problems")
    @GetMapping("/public/problem/getAll")
    public List<Problem> getAllProblems() {
        return problemService.getAllProblems();
    }

    @Operation(summary = "Get a problem by its id")
    @GetMapping("/public/problem/{problemId}")
    public ResponseEntity<Problem> getProblemById(@PathVariable("problemId") int problemId) {
        Optional<Problem> problemOptional = problemService.getProblemById(problemId);
        return problemOptional.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create a problem")
    @PostMapping("/public/problem")
    public ResponseEntity<Problem> createProblem(@RequestBody Problem problem) {
        Problem saved = problemService.createProblem(problem);
        return ResponseEntity.ok(saved);
    }

    @Operation(summary = "Update a problem")
    @PatchMapping("/advanced/problem/{problemId}")
    public ResponseEntity<Problem> updateReport(
            @PathVariable("problemId") int problemId,
            @RequestBody Problem updatedProblem
    ) {
        return ResponseEntity.ok(problemService.updateProblem(problemId, updatedProblem));
    }

    @Operation(summary = "Delete a problem")
    @DeleteMapping("/advanced/problem/{problemId}")
    public ResponseEntity<String> deleteProblem(@PathVariable("problemId") int problemId) {
        return problemService.deleteProblem(problemId);
    }


    @Operation(summary = "Get a problem by its id")
    @GetMapping("/advanced/getStaffProblems/{userId}")
    public ResponseEntity<?> getStaffProblems(@PathVariable("userId") int userId) {
        Optional<User> userOptional = userService.getUserById(userId);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Korisnik nije pronaden");
        }
        User user = userOptional.get();
        return ResponseEntity.ok(userService.getProblemsForUser(user));
    }

}
