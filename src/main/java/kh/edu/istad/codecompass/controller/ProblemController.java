package kh.edu.istad.codecompass.controller;

import jakarta.validation.Valid;
import kh.edu.istad.codecompass.dto.problem.request.CreateProblemRequest;
import kh.edu.istad.codecompass.dto.problem.response.ProblemResponse;
import kh.edu.istad.codecompass.dto.problem.response.ProblemResponseBySpecificUser;
import kh.edu.istad.codecompass.dto.problem.request.UpdateProblemRequest;
import kh.edu.istad.codecompass.dto.problem.response.ProblemSummaryResponse;
import kh.edu.istad.codecompass.service.ProblemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/code-compass/problems")
//@PreAuthorize("hasAnyRole('CREATOR', 'ADMIN')")
public class ProblemController {
    private final ProblemService problemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProblemResponse createProblem(
            @RequestBody @Valid
            CreateProblemRequest problemRequest,

            @AuthenticationPrincipal
            Jwt jwt
    ) {
        String username = jwt.getClaim("preferred_username");
        return problemService.createProblem(problemRequest, username);
    }

    @GetMapping("/{problemId}/me")
    public ProblemResponseBySpecificUser getProblemBySpecificUser(
            @PathVariable
            Long problemId,

            @AuthenticationPrincipal
            Jwt jwt
    ) {
        String username = jwt.getClaim("preferred_username");
        return problemService.getProblemBySpecificUser(username, problemId);
    }


    @GetMapping("/{problemId}")
    public ProblemResponse findProblemById(@PathVariable Long problemId) {
        return problemService.getProblem(problemId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{problemId}/verification")
    ResponseEntity<String> verifyProblem(
            @PathVariable
            Long problemId,

            @RequestParam(defaultValue = "true")
            boolean verified
    ) {
        problemService.verifyProblem(problemId, verified);
        return ResponseEntity.ok("The problem has been verified successfully");
    }

    @PatchMapping("/{problemId}")
    ResponseEntity<String> updateCreatedProblem(
            @PathVariable
            Long problemId,

            @RequestBody
            @Valid
            UpdateProblemRequest updateProblemRequest,
            @AuthenticationPrincipal
            Jwt jwt
    ) {

        String authorUsername = jwt.getClaim("preferred_username");

        problemService.updateProblem(problemId, authorUsername, updateProblemRequest);

        return ResponseEntity.ok("The problem has been updated successfully");

    }

    @GetMapping("/unverified")
    public List<ProblemSummaryResponse> getUnverifiedProblems() {
        return problemService.getUnverifiedProblems();
    }

    @GetMapping
    public List<ProblemSummaryResponse> getAllProblems() {
        return problemService.getProblems();
    }

    @GetMapping("/verified")
    public List<ProblemSummaryResponse> getVerifiedProblems() {
        return problemService.getVerifiedProblems();
    }

}
