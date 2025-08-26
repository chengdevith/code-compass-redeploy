package kh.edu.istad.codecompass.controller;

import jakarta.validation.Valid;
import kh.edu.istad.codecompass.dto.jugde0.request.BatchSubmissionRequest;
import kh.edu.istad.codecompass.dto.jugde0.request.CreateSubmissionRequest;
import kh.edu.istad.codecompass.dto.jugde0.response.Judge0BatchResponse;
import kh.edu.istad.codecompass.dto.jugde0.response.Judge0SubmissionResponse;
import kh.edu.istad.codecompass.dto.jugde0.response.SubmissionResult;
import kh.edu.istad.codecompass.service.Judge0Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/code-compass/submissions")
@CrossOrigin(origins = {"http://localhost:3000"})
public class Judge0Controller {

    private final Judge0Service judge0Service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SubmissionResult execute(@RequestBody @Valid CreateSubmissionRequest request) {
        return judge0Service.createSubmission(request);
    }

    @GetMapping("/{token}")
    public Judge0SubmissionResponse getSubmission(@PathVariable String token) {
        return judge0Service.getSubmissionByToken(token);
    }

    @PostMapping("/batch/{problemId}")
    @ResponseStatus(HttpStatus.CREATED)
    public Judge0BatchResponse executeBatch(
            @RequestBody
            @Valid
            BatchSubmissionRequest request,

            @PathVariable Long problemId,
            @AuthenticationPrincipal Jwt jwt
    ) {

        String username = jwt.getClaim("preferred_username");

        return judge0Service.createSubmissionBatch(request, username, problemId);
    }

    @PostMapping("/run/batch")
    public Judge0BatchResponse executeRun(@RequestBody @Valid BatchSubmissionRequest request) {
        return judge0Service.runSubmissionBatch(request);
    }

}

