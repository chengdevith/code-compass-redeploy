package kh.edu.istad.codecompass.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
    @Operation(summary = "Creates a new problem", security = {@SecurityRequirement(name = "bearerAuth")})
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
    @Operation(summary = "Access to different problem details for different user", security = {@SecurityRequirement(name = "bearerAuth")})
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
    @Operation(summary = "Access to a problem (public)")
    @PreAuthorize("permitAll()")
    public ProblemResponse findProblemById(@PathVariable Long problemId) {
        return problemService.getProblem(problemId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Verifies a problem to be created", security = {@SecurityRequirement(name = "bearerAuth")})
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
    @Operation(summary = "Updates a specific problem", security = {@SecurityRequirement(name = "bearerAuth")})
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
    @Operation(summary = "Acts as a filter for admin to access all unverified problems", security = {@SecurityRequirement(name = "bearerAuth")})
    public List<ProblemSummaryResponse> getUnverifiedProblems() {
        return problemService.getUnverifiedProblems();
    }

    @GetMapping
    @Operation(summary = "For admin to view all problems - both verified and unverified", security = {@SecurityRequirement(name = "bearerAuth")})
    public List<ProblemSummaryResponse> getAllProblems() {
        return problemService.getProblems();
    }

    @GetMapping("/verified")
    @Operation(summary = "Displays verified problems (public)", security = {@SecurityRequirement(name = "bearerAuth")})
    public List<ProblemSummaryResponse> getVerifiedProblems() {
        return problemService.getVerifiedProblems();
    }

}
