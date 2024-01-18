package fer.proinz.prijave.controller;

import fer.proinz.prijave.exception.NonExistingCityDeptException;
import fer.proinz.prijave.exception.NonExistingProblemException;
import fer.proinz.prijave.exception.NonExistingReportException;
import fer.proinz.prijave.model.Problem;
import fer.proinz.prijave.service.ProblemService;
import fer.proinz.prijave.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<Problem> getProblemById(@PathVariable("problemId") int problemId)
            throws NonExistingProblemException {
        return ResponseEntity.ok(problemService
                .getProblemById(problemId)
                .orElseThrow(NonExistingProblemException::new)
        );
    }

    @Operation(summary = "Get problems for a certain city dept")
    @GetMapping("/advanced/problem/{cityDeptId}")
    public ResponseEntity<List<Problem>> getProblemsForCityDept(@PathVariable("cityDeptId") int cityDeptId)
            throws NonExistingCityDeptException {
        return ResponseEntity.ok(problemService.getProblemsForCityDept(cityDeptId));
    }

    @Operation(summary = "Create a problem")
    @PostMapping("/public/problem")
    public ResponseEntity<Problem> createProblem(@RequestBody Problem problem) {
        return ResponseEntity.ok(problemService.createProblem(problem));
    }

    @Operation(summary = "Update a problem")
    @PatchMapping("/advanced/problem/{problemId}")
    public ResponseEntity<Problem> updateReport(
            @PathVariable("problemId") int problemId,
            @RequestBody Problem updatedProblem
    ) throws NonExistingProblemException {
        return ResponseEntity.ok(problemService.updateProblem(problemId, updatedProblem));
    }

    @Operation(summary = "Staff member groups reports")
    @PatchMapping("/advanced/report/group/{problemId}")
    public ResponseEntity<Problem> groupReports(
            @PathVariable("problemId") int problemId,
            @RequestBody List<Integer> reportIdList
    ) throws NonExistingProblemException, NonExistingReportException {
        return ResponseEntity.ok(problemService.groupReports(problemId, reportIdList));
    }

    @Operation(summary = "Delete a problem")
    @DeleteMapping("/advanced/problem/{problemId}")
    public ResponseEntity<String> deleteProblem(@PathVariable("problemId") int problemId)
            throws NonExistingProblemException {
        return problemService.deleteProblem(problemId);
    }

}
