package kh.edu.istad.codecompass.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import kh.edu.istad.codecompass.dto.solution.SolutionRequest;
import kh.edu.istad.codecompass.dto.solution.SolutionResponse;
import kh.edu.istad.codecompass.service.SolutionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/solutions")
@RequiredArgsConstructor
public class SolutionController {

    private final SolutionService solutionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "For subscribers and creators to post their problem solution (secured)", security = {@SecurityRequirement(name = "bearerAuth")})
    public SolutionResponse postSolution(@RequestBody @Valid SolutionRequest solutionRequest, @AuthenticationPrincipal Jwt jwt) {
        String author = jwt.getClaim("preferred_username");
        return solutionService.postSolution(solutionRequest, author);
    }

    @GetMapping("/problem/{problemId}")
    @Operation(summary = "Access solutions in a specific problem after they've solved it (secured)", security = {@SecurityRequirement(name = "bearerAuth")})
    public List<SolutionResponse> getAllSolutions(@AuthenticationPrincipal Jwt jwt, @PathVariable Long problemId) {
        String username = jwt.getClaim("preferred_username");
        return solutionService.getAllSolutions(username, problemId);
    }

}
