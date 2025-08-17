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
import reactor.core.publisher.Mono;

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
    public Mono<BatchSubmissionTokenResponse> createBatchSubmissions(
            @Valid @RequestBody BatchSubmissionRequest request) {
            return judge0Service.createSubmissionBatch(request).map(res ->
            {
                List<String> tokens = res.submissions().stream().map(Judge0SubmissionResponse::token).toList();
                return new BatchSubmissionTokenResponse(tokens);
            });
    }

    @GetMapping("/batch")
    public Mono<Judge0BatchResponse> getBatchSubmissions(
            @RequestParam String tokens,
            @RequestParam(defaultValue = "false") boolean base64_encoded,
            @RequestParam(defaultValue = "language_id,stdout,time,memory,stderr,token,compile_output,message,status") String fields) {

        return judge0Service.getBatchSubmissions(tokens, base64_encoded, fields);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ResponseEntity<SubmissionTokenResponse>> createSubmission(
            @Valid @RequestBody CreateSubmissionRequest request) {

        log.info("Received submission request for language: {}", request.languageId());

        return judge0Service.createSubmission(request)
                .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response))
                .doOnSuccess(response -> {
                    assert response.getBody() != null;
                    log.info("Submission created: {}", response.getBody().token());
                });
    }

    @GetMapping("/{token}")
    public Mono<ResponseEntity<SubmissionResult>> getSubmission(@PathVariable String token) {
        return judge0Service.getSubmissionByToken(token)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/{token}/poll")
    public Mono<ResponseEntity<SubmissionResult>> pollSubmission(
            @PathVariable String token,
            @RequestParam(defaultValue = "30") int maxAttempts,
            @RequestParam(defaultValue = "1000") long intervalMs) {

        return judge0Service.pollSubmissionUntilComplete(token, maxAttempts, intervalMs)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/{token}/local")
    public Mono<ResponseEntity<SubmissionResult>> getLocalSubmission(@PathVariable String token) {
        return judge0Service.findSubmissionByToken(token)
                .map(ResponseEntity::ok);
    }
}
