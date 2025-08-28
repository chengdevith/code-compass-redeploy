package kh.edu.istad.codecompass.controller;

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
@RequestMapping("/api/v1/code-compass/solutions")
@RequiredArgsConstructor
public class SolutionController {

    private final SolutionService solutionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SolutionResponse postSolution(@RequestBody @Valid SolutionRequest solutionRequest, @AuthenticationPrincipal Jwt jwt) {
        String author = jwt.getClaim("preferred_username");
        return solutionService.postSolution(solutionRequest, author);
    }

    @GetMapping
    public List<SolutionResponse> getAllSolutions() {
        return solutionService.getAllSolutions();
    }

}
