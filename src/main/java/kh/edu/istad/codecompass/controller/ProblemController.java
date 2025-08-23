package kh.edu.istad.codecompass.controller;

import kh.edu.istad.codecompass.dto.problem.CreateProblemRequest;
import kh.edu.istad.codecompass.dto.problem.ProblemResponse;
import kh.edu.istad.codecompass.service.ProblemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/code-compass/problems")
public class ProblemController {
    private final ProblemService problemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProblemResponse createProblem(@RequestBody CreateProblemRequest problemRequest) {
        return problemService.createProblem(problemRequest);
    }

    @GetMapping("/{problemId}")
    public ProblemResponse findProblemById(@PathVariable Long problemId) {
        return problemService.getProblem(problemId);
    }
}
