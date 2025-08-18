package kh.edu.istad.codecompass.controller;

import jakarta.validation.Valid;
import kh.edu.istad.codecompass.dto.jugde0.*;
import kh.edu.istad.codecompass.service.Judge0Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/submissions")
@Validated
@Slf4j
public class Judge0Controller {

    private final Judge0Service judge0Service;

    @PostMapping("/batch")
    @ResponseStatus(HttpStatus.CREATED)
    public BatchSubmissionTokenResponse createBatchSubmissions(
            @Valid @RequestBody BatchSubmissionRequest request) {

        Judge0BatchResponse response = judge0Service.createSubmissionBatch(request);
        List<String> tokens = response.submissions().stream()
                .map(Judge0SubmissionResponse::token)
                .toList();

        return new BatchSubmissionTokenResponse(tokens);
    }

    @GetMapping("/batch")
    public Judge0BatchResponse getBatchSubmissions(
            @RequestParam String tokens,
            @RequestParam(defaultValue = "false") boolean base64_encoded,
            @RequestParam(defaultValue = "language_id,stdout,time,memory,stderr,token,compile_output,message,status") String fields) {

        return judge0Service.getBatchSubmissions(tokens, base64_encoded, fields);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<SubmissionTokenResponse> createSubmission(
            @Valid @RequestBody CreateSubmissionRequest request) {

        log.info("Received submission request for language: {}", request.languageId());

        SubmissionTokenResponse response = judge0Service.createSubmission(request);

        log.info("Submission created: {}", response.token());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{token}")
    public ResponseEntity<SubmissionResult> getSubmission(@PathVariable String token) {
        SubmissionResult result = judge0Service.getSubmissionByToken(token);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{token}/poll")
    public ResponseEntity<SubmissionResult> pollSubmission(
            @PathVariable String token,
            @RequestParam(defaultValue = "30") int maxAttempts,
            @RequestParam(defaultValue = "1000") long intervalMs) {

        SubmissionResult result = judge0Service.pollSubmissionUntilComplete(token, maxAttempts, intervalMs);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{token}/local")
    public ResponseEntity<SubmissionResult> getLocalSubmission(@PathVariable String token) {
        SubmissionResult result = judge0Service.findSubmissionByToken(token);
        return ResponseEntity.ok(result);
    }
}
