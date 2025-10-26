package kh.edu.istad.codecompass.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import kh.edu.istad.codecompass.dto.solution.SolutionRequest;
import kh.edu.istad.codecompass.dto.solution.SolutionResponse;
import kh.edu.istad.codecompass.service.SolutionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/solutions")
@RequiredArgsConstructor
public class SolutionController {

    private final SolutionService solutionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Post solution | [ SUBSCRIBER, CREATOR ] (secured)", security = {@SecurityRequirement(name = "bearerAuth")})
    public SolutionResponse postSolution(@RequestBody @Valid SolutionRequest solutionRequest, @AuthenticationPrincipal Jwt jwt) {
        String author = jwt.getClaim("preferred_username");
        return solutionService.postSolution(solutionRequest, author);
    }

    @GetMapping("/problem/{problemId}")
    @Operation(summary = "Get solutions | [ SUBSCRIBER, CREATOR ] (secured)", security = {@SecurityRequirement(name = "bearerAuth")})
    public Page<SolutionResponse> getAllSolutions(@AuthenticationPrincipal Jwt jwt,
                                                  @PathVariable Long problemId,
                                                  @RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "30") int size) {
        String username = jwt.getClaim("preferred_username");
        Pageable pageable = PageRequest.of(page, size, Sort.by("id"));
        return solutionService.getAllSolutions(username, problemId, pageable);
    }

    @DeleteMapping("/{solutionId}/delete")
    @PreAuthorize("hasAnyRole('SUBSCRIBER', 'CREATOR')")
    @Operation(summary = "Delete a solution | [ SUBSCRIBER, CREATOR ] (secured)", security = {@SecurityRequirement(name = "bearerAuth")})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSolution(@PathVariable Long solutionId, @AuthenticationPrincipal Jwt jwt) {
        String author = jwt.getClaim("preferred_username");
        solutionService.deleteSolution(solutionId, author);
    }

}
