package kh.edu.istad.codecompass.controller;

import kh.edu.istad.codecompass.dto.problem.CreateProblemRequest;
import kh.edu.istad.codecompass.dto.problem.ProblemResponse;
import kh.edu.istad.codecompass.service.ProblemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/code-compass/problems")
//@PreAuthorize("hasAnyRole('CREATOR', 'ADMIN')")
public class ProblemController {
    private final ProblemService problemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProblemResponse createProblem(@RequestBody CreateProblemRequest problemRequest, @AuthenticationPrincipal Jwt jwt) {
        String username = jwt.getClaim("preferred_username");
        return problemService.createProblem(problemRequest, username);
    }

    @GetMapping("/{problemId}")
    public ProblemResponse findProblemById(@PathVariable Long problemId) {
        return problemService.getProblem(problemId);
    }

    @PatchMapping("/{problemId}/verification")
    ResponseEntity<String> verifyProblem(
            @PathVariable Long problemId,
            @RequestParam(defaultValue = "true") boolean verified
    ) {
        problemService.verifyProblem(problemId, verified);
        return ResponseEntity.ok("The problem has been verified successfully");
    }

}
