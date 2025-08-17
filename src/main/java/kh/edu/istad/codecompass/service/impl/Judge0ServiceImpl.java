package kh.edu.istad.codecompass.service.impl;

import kh.edu.istad.codecompass.domain.Submission;
import kh.edu.istad.codecompass.dto.jugde0.*;
import kh.edu.istad.codecompass.mapper.Judge0Mapper;
import kh.edu.istad.codecompass.repository.SubmissionRepository;
import kh.edu.istad.codecompass.service.Judge0Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class Judge0ServiceImpl implements Judge0Service {

    private final WebClient judge0WebClient;
    private final SubmissionRepository submissionRepository;
    private final Judge0Mapper mapper;

    @Override
    public Mono<SubmissionTokenResponse> createSubmission(CreateSubmissionRequest request) {
        log.info("Creating submission for language: {}", request.languageId());

        return judge0WebClient.post()
                .uri("/submissions")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, this::handleClientError)
                .onStatus(HttpStatusCode::is5xxServerError, this::handleServerError)
                .bodyToMono(SubmissionTokenResponse.class)
                .flatMap(tokenResponse -> saveInitialSubmission(request, tokenResponse.token())
                        .map(saved -> tokenResponse))
                .doOnSuccess(response -> log.info("Submission created with token: {}", response.token()))
                .doOnError(e -> log.error("Error creating submission", e));
    }

    @Override
    public Mono<Judge0BatchResponse> createSubmissionBatch(BatchSubmissionRequest batchRequest) {
        log.info("Creating batch submissions for language: {}", batchRequest.languageId());

        List<CreateSubmissionRequest> submissions = prepareSubmissionRequests(batchRequest);
        Judge0BatchRequest judge0BatchRequest = new Judge0BatchRequest(submissions);

        return sendBatchRequestToJudge0(judge0BatchRequest)
                .flatMap(response -> saveSubmissionsToDatabase(response, batchRequest.languageId()));
    }

    private List<CreateSubmissionRequest> prepareSubmissionRequests(BatchSubmissionRequest batchRequest) {
        List<CreateSubmissionRequest> requests = new ArrayList<>();

        for (int i = 0; i < batchRequest.inputs().size(); i++) {
            requests.add(CreateSubmissionRequest.builder()
                    .sourceCode(batchRequest.sourceCode())
                    .languageId(batchRequest.languageId())
                    .stdin(batchRequest.inputs().get(i))
                    .expectedOutput(i < batchRequest.expectedOutputs().size()
                            ? batchRequest.expectedOutputs().get(i)
                            : null)
                    .build());
        }
        return requests;
    }

    private Mono<Judge0BatchResponse> sendBatchRequestToJudge0(Judge0BatchRequest request) {
        return judge0WebClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/submissions/batch")
                        .queryParam("base64_encoded", request.base64Encoded())
                        .build())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse ->
                        clientResponse.bodyToMono(String.class)
                                .flatMap(error -> Mono.error(new RuntimeException(
                                        "Judge0 API error: " + error))))
                .bodyToMono(Judge0SubmissionResponse[].class)
                .map(Arrays::asList)
                .map(Judge0BatchResponse::new);
    }

    private Mono<Judge0BatchResponse> saveSubmissionsToDatabase(Judge0BatchResponse response, String languageId) {
        return Mono.fromCallable(() -> {
                    List<Submission> submissions = response.submissions().stream()
                            .map(judgeResponse -> mapper.fromJudge0ResponseToEntity(judgeResponse, languageId))
                            .collect(Collectors.toList());
                    return submissionRepository.saveAll(submissions);
                })
                .subscribeOn(Schedulers.boundedElastic())
                .thenReturn(response);
    }

    @Override
    public Mono<SubmissionResult> getSubmissionByToken(String token) {
        log.debug("Getting submission status for token: {}", token);

        return judge0WebClient.get()
                .uri("/submissions/{token}", token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, this::handleClientError)
                .onStatus(HttpStatusCode::is5xxServerError, this::handleServerError)
                .bodyToMono(Judge0SubmissionResponse.class)
                .flatMap(this::updateSubmissionFromResponse)
                .doOnSuccess(result -> log.debug("Retrieved submission: {} - {}",
                        token, result.status().description()))
                .doOnError(e -> log.error("Error getting submission: {}", token, e));
    }

    @Override
    public Mono<Judge0BatchResponse> getBatchSubmissions(String tokens, boolean base64Encoded, String fields) {
        log.info("Getting batch submissions for tokens: {}", tokens);

        return judge0WebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/submissions/batch")
                        .queryParam("tokens", tokens)
                        .queryParam("base64_encoded", base64Encoded)
                        .queryParam("fields", fields)
                        .build())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, this::handleClientError)
                .onStatus(HttpStatusCode::is5xxServerError, this::handleServerError)
                .bodyToMono(Judge0BatchResponse.class)
                .doOnSuccess(response -> log.info("Retrieved {} submissions", response.submissions().size()))
                .doOnError(e -> log.error("Error getting batch submissions for tokens: {}", tokens, e));
    }

    @Override
    public Mono<SubmissionResult> pollSubmissionUntilComplete(String token) {
        return pollSubmissionUntilComplete(token, 30, 1000); // 30 attempts, 1 second interval
    }

    @Override
    public Mono<SubmissionResult> pollSubmissionUntilComplete(String token, int maxAttempts, long intervalMs) {
        return Mono.defer(() -> getSubmissionByToken(token))
                .flatMap(result -> {
                    Integer statusId = result.status().id();
                    if (statusId != null && statusId != 1 && statusId != 2) {
                        return Mono.just(result); // Completed
                    }
                    if (maxAttempts <= 1) {
                        return findSubmissionByToken(token); // Fallback to DB
                    }
                    return Mono.delay(Duration.ofMillis(intervalMs))
                            .flatMap(i -> pollSubmissionUntilComplete(token, maxAttempts - 1, intervalMs));
                });
    }

    @Override
    public Mono<SubmissionResult> findSubmissionByToken(String token) {
        return Mono.fromCallable(() -> {
            Submission submission = submissionRepository.findByToken(token)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Submission not found: " + token));
            return mapper.fromEntityToResult(submission);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    private Mono<Submission> saveInitialSubmission(CreateSubmissionRequest request, String token) {
        return Mono.fromCallable(() -> {
            Submission submission = Submission.builder()
                    .token(token)
                    .languageId(request.languageId())
                    .status("In Queue")
                    .statusId(1)
                    .build();
            return submissionRepository.save(submission);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    private Mono<SubmissionResult> updateSubmissionFromResponse(Judge0SubmissionResponse response) {
        return Mono.fromCallable(() -> {
            Optional<Submission> existingOpt = submissionRepository.findByToken(response.token());

            Submission submission;
            if (existingOpt.isPresent()) {
                submission = existingOpt.get();
                submission.setStdout(response.stdout());
                submission.setStderr(response.stderr());
                submission.setCompileOutput(response.compileOutput());
                submission.setMessage(response.message());
                submission.setTime(response.time());
                submission.setLanguageId(response.languageId());
                submission.setMemory(response.memory());
                submission.setStatus(response.status().description());
                submission.setStatusId(response.status().id());
            } else
                submission = mapper.fromJudge0ResponseToEntity(response);

            submission = submissionRepository.save(submission);
            return mapper.fromEntityToResult(submission);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    private Mono<Throwable> handleClientError(ClientResponse response) {
        return response.bodyToMono(String.class)
                .flatMap(body -> {
                    log.error("Judge0 client error {}: {}", response.statusCode(), body);
                    return Mono.error(new ResponseStatusException(
                            HttpStatus.BAD_REQUEST, "Judge0 error: " + body));
                });
    }

    private Mono<Throwable> handleServerError(ClientResponse response) {
        return response.bodyToMono(String.class)
                .flatMap(body -> {
                    log.error("Judge0 server error {}: {}", response.statusCode(), body);
                    return Mono.error(new ResponseStatusException(
                            HttpStatus.INTERNAL_SERVER_ERROR, "Judge0 server error"));
                });
    }
}
