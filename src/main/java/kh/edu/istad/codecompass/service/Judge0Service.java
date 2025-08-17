package kh.edu.istad.codecompass.service;

import kh.edu.istad.codecompass.dto.jugde0.*;
import reactor.core.publisher.Mono;

public interface Judge0Service {
//    single submission
    Mono<SubmissionTokenResponse> createSubmission(CreateSubmissionRequest request);
    Mono<SubmissionResult> getSubmissionByToken(String token);
    Mono<SubmissionResult> pollSubmissionUntilComplete(String token);
    Mono<SubmissionResult> pollSubmissionUntilComplete(String token, int maxAttempts, long intervalMs);
    Mono<SubmissionResult> findSubmissionByToken(String token);

//    batch submission
    Mono<Judge0BatchResponse> createSubmissionBatch(BatchSubmissionRequest batchRequest);
    Mono<Judge0BatchResponse> getBatchSubmissions(String tokens, boolean base64Encoded, String fields);
}
