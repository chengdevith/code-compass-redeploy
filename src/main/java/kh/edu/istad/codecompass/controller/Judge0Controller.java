package kh.edu.istad.codecompass.controller;

import jakarta.validation.Valid;
import kh.edu.istad.codecompass.dto.jugde0.*;
import kh.edu.istad.codecompass.service.Judge0Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/submissions")
@CrossOrigin(origins = {"http://localhost:3000"})
public class Judge0Controller {

    private final Judge0Service judge0Service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SubmissionResult execute(@RequestBody @Valid CreateSubmissionRequest request) {
        return judge0Service.createSubmission(request);
    }

    @GetMapping("/{token}")
    public SubmissionResult getSubmission(@PathVariable String token) {
        return judge0Service.getSubmissionByToken(token);
    }

    @PostMapping("/batch")
    @ResponseStatus(HttpStatus.CREATED)
    public Judge0BatchResponse executeBatch(@RequestBody @Valid BatchSubmissionRequest request) {
        return judge0Service.createSubmissionBatch(request);
    }

    @PostMapping("/run")
    public ResponseEntity<Judge0SingleResponse> executeSingle(@RequestBody @Valid SingleSubmissionRequest request) {
        try {
            log.info("Received single execution request: language={}", request.languageId());

            // Convert single request to batch with one item
            BatchSubmissionRequest batchRequest = new BatchSubmissionRequest(
                    request.sourceCode(),
                    request.languageId(),
                    List.of(request.stdin()),
                    request.expectedOutput() != null ? List.of(request.expectedOutput()) : List.of()
            );

            Judge0BatchResponse batchResponse = judge0Service.createSubmissionBatch(batchRequest);

            // Return first (and only) result
            if (!batchResponse.submissions().isEmpty()) {
                Judge0SubmissionResponse submission = batchResponse.submissions().get(0);
                Judge0SingleResponse response = new Judge0SingleResponse(
                        submission.languageId(),
                        submission.stdout(),
                        submission.time(),
                        submission.memory(),
                        submission.stderr(),
                        submission.token(),
                        submission.compileOutput(),
                        submission.message(),
                        submission.status()
                );
                return ResponseEntity.ok(response);
            }

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorSingleResponse("No response from Judge0"));

        } catch (Exception e) {
            log.error("Error executing single submission: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorSingleResponse(e.getMessage()));
        }
    }
    private Judge0BatchResponse createErrorResponse(String errorMessage) {
        List<Judge0SubmissionResponse> errorSubmissions = List.of(
                new Judge0SubmissionResponse(
                        null,
                        null,
                        null,
                        null,
                        errorMessage,
                        null,
                        null,
                        errorMessage,
                        new Judge0SubmissionResponse.Status(13, "Internal Error")
                )
        );
        return new Judge0BatchResponse(errorSubmissions);
    }

    private Judge0SingleResponse createErrorSingleResponse(String errorMessage) {
        return new Judge0SingleResponse(
                null,
                null,
                null,
                null,
                errorMessage,
                null,
                null,
                errorMessage,
                new Judge0SubmissionResponse.Status(13, "Internal Error")
        );
    }
}

