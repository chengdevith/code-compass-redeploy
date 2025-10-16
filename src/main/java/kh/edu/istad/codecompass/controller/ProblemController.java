package kh.edu.istad.codecompass.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import kh.edu.istad.codecompass.dto.problem.request.CreateProblemRequest;
import kh.edu.istad.codecompass.dto.problem.response.ProblemResponse;
import kh.edu.istad.codecompass.dto.problem.response.ProblemResponseBySpecificUser;
import kh.edu.istad.codecompass.dto.problem.request.UpdateProblemRequest;
import kh.edu.istad.codecompass.dto.problem.response.ProblemSummaryResponse;
import kh.edu.istad.codecompass.elasticsearch.domain.ProblemIndex;
import kh.edu.istad.codecompass.elasticsearch.service.ProblemIndexService;
import kh.edu.istad.codecompass.service.ProblemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/problems")
public class ProblemController {

    private final ProblemIndexService problemIndexService;
    private final ProblemService problemService;

    @PostMapping
    @Operation(summary = "Creates a new problem (secured)", security = {@SecurityRequirement(name = "bearerAuth")})
    @PreAuthorize("hasAnyRole('ADMIN', 'CREATOR')")
    @ResponseStatus(HttpStatus.CREATED)
    public ProblemResponse createProblem(
            @RequestBody @Valid CreateProblemRequest problemRequest,
            @AuthenticationPrincipal Jwt jwt
    ) {
        String username = jwt.getClaim("preferred_username");
        return problemService.createProblem(problemRequest, username);
    }

    @GetMapping("/{problemId}/me")
    @Operation(summary = "Access to different problem details for different user (secured)", security = {@SecurityRequirement(name = "bearerAuth")})
    @PreAuthorize("hasAnyRole('SUBSCRIBER', 'CREATOR')")
    public ProblemResponseBySpecificUser getProblemBySpecificUser(
            @PathVariable Long problemId,
            @AuthenticationPrincipal Jwt jwt
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

    @PutMapping("/{problemId}/verification")
    @Operation(summary = "Verifies a problem to be created (secured)", security = {@SecurityRequirement(name = "bearerAuth")})
    @PreAuthorize("hasRole('ADMIN')")
    public ProblemResponse verifyProblem(
            @PathVariable Long problemId,
            @RequestParam(defaultValue = "true") boolean verified
    ) {
        return problemService.verifyProblem(problemId, verified);
    }

    @PatchMapping("/{problemId}")
    @Operation(summary = "Updates a specific problem (secured)", security = {@SecurityRequirement(name = "bearerAuth")})
    @PreAuthorize("hasAnyRole('ADMIN', 'CREATOR')")
    public ResponseEntity<String> updateCreatedProblem(
            @PathVariable Long problemId,
            @RequestBody @Valid UpdateProblemRequest updateProblemRequest,
            @AuthenticationPrincipal Jwt jwt
    ) {
        String authorUsername = jwt.getClaim("preferred_username");
        problemService.updateProblem(problemId, authorUsername, updateProblemRequest);
        return ResponseEntity.ok("The problem has been updated successfully");
    }

    @GetMapping("/unverified")
    @Operation(summary = "View all unverified problems | [ ADMIN ] (secured)", security = {@SecurityRequirement(name = "bearerAuth")})
    @PreAuthorize("hasRole('ADMIN')")
    public Page<ProblemSummaryResponse> getUnverifiedProblems(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "100") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id"));
        return problemService.getUnverifiedProblems(pageable);
    }

    @GetMapping
    @Operation(summary = "View all problems - both verified and unverified | [ ADMIN ] (secured)", security = {@SecurityRequirement(name = "bearerAuth")})
    @PreAuthorize("hasRole('ADMIN')")
    public List<ProblemSummaryResponse> getAllProblems() {
        return problemService.getProblems();
    }

    @GetMapping("/verified")
    @Operation(summary = "Displays verified problems (public)")
    @PreAuthorize("permitAll()")
    public Page<ProblemSummaryResponse> getVerifiedProblems(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "100") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id"));
        return problemService.getVerifiedProblems(pageable);
    }

    @GetMapping("/search")
    @Operation(summary = "Search problems (public)")
    @PreAuthorize("permitAll()")
    public Page<ProblemIndex> searchProblems(@RequestParam String keyword, @RequestParam(defaultValue = "50") int size, @RequestParam(defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, size);
        return problemIndexService.searchProblem(keyword, pageable);
    }

    @GetMapping("/me")
    @Operation(summary = "Get problems by creator | [ CREATOR, ADMIN ] (secured)", security = {@SecurityRequirement(name = "bearerAuth")})
    public List<ProblemResponse> getProblemsByAuthor (@AuthenticationPrincipal Jwt jwt) {
        String username = jwt.getClaim("preferred_username");
        return problemService.getProblemsByAuthor(username);
    }

    @DeleteMapping("/{problemId}/delete")
    @PreAuthorize("hasAnyRole('CREATOR', 'ADMIN')")
    @Operation(summary = "Delete a problem by ID | [ ADMIN ]", security = {@SecurityRequirement(name = "bearerAuth")})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProblemById(@PathVariable Long problemId, @AuthenticationPrincipal Jwt jwt) {
        String username = jwt.getClaim("preferred_username");
        problemService.deleteProblemById(problemId, username);
    }

    @PutMapping("/{problemId}/rejection")
    @Operation(summary = "Reject a problem by ID | [ ADMIN ]", security = {@SecurityRequirement(name = "bearerAuth")})
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void rejectProblemById(@PathVariable Long problemId) {
        problemService.rejectProblemById(problemId);
    }

    @GetMapping("/tags")
    @Operation(summary = "Get all problems' tags (public)", security = {@SecurityRequirement(name = "bearerAuth")})
    List<String> getAllProblemTags() {
        return problemService.getAllProblemTags();
    }
}
