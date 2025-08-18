package kh.edu.istad.codecompass.service.impl;

import kh.edu.istad.codecompass.domain.Submission;
import kh.edu.istad.codecompass.dto.jugde0.*;
import kh.edu.istad.codecompass.mapper.Judge0Mapper;
import kh.edu.istad.codecompass.repository.SubmissionRepository;
import kh.edu.istad.codecompass.service.Judge0Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class Judge0ServiceImpl implements Judge0Service {

    private final RestTemplate judge0RestTemplate;
    private final SubmissionRepository submissionRepository;
    private final Judge0Mapper mapper;

    @Override
    public SubmissionTokenResponse createSubmission(CreateSubmissionRequest request) {
        log.info("Creating submission for language: {}", request.languageId());

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<CreateSubmissionRequest> httpEntity = new HttpEntity<>(request, headers);

            ResponseEntity<SubmissionTokenResponse> response = judge0RestTemplate.exchange(
                    "/submissions",
                    HttpMethod.POST,
                    httpEntity,
                    SubmissionTokenResponse.class
            );

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new ResponseStatusException(
                        HttpStatus.valueOf(response.getStatusCode().value()),
                        "Judge0 API error"
                );
            }

            SubmissionTokenResponse tokenResponse = response.getBody();
            if (tokenResponse == null) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Empty response from Judge0");
            }

            // Save initial submission
            saveInitialSubmission(request, tokenResponse.token());

            log.info("Submission created with token: {}", tokenResponse.token());
            return tokenResponse;

        } catch (HttpClientErrorException e) {
            log.error("Judge0 client error: {}", e.getResponseBodyAsString());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Judge0 error: " + e.getResponseBodyAsString());
        } catch (HttpServerErrorException e) {
            log.error("Judge0 server error: {}", e.getResponseBodyAsString());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Judge0 server error");
        } catch (Exception e) {
            log.error("Error creating submission", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Submission creation failed");
        }
    }

    @Override
    public Judge0BatchResponse createSubmissionBatch(BatchSubmissionRequest batchRequest) {
        log.info("Creating batch submissions for language: {}", batchRequest.languageId());

        try {
            List<CreateSubmissionRequest> submissions = prepareSubmissionRequests(batchRequest);
            Judge0BatchRequest judge0BatchRequest = new Judge0BatchRequest(submissions);

            Judge0BatchResponse response = sendBatchRequestToJudge0(judge0BatchRequest);
            return saveSubmissionsToDatabase(response, batchRequest.languageId());

        } catch (Exception e) {
            log.error("Error creating batch submissions", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Batch submission creation failed");
        }
    }

    private List<CreateSubmissionRequest> prepareSubmissionRequests(BatchSubmissionRequest batchRequest) {
        List<CreateSubmissionRequest> requests = new ArrayList<>();

        if (batchRequest.inputs() == null || batchRequest.inputs().isEmpty()) {
            log.warn("Batch request has no inputs. Adding a default empty submission to avoid Judge0 error.");
            requests.add(CreateSubmissionRequest.builder()
                    .sourceCode(batchRequest.sourceCode())
                    .languageId(batchRequest.languageId())
                    .stdin("")
                    .expectedOutput("")
                    .build());
            return requests;
        }

        for (int i = 0; i < batchRequest.inputs().size(); i++) {
            String expected = (batchRequest.expectedOutputs() != null && i < batchRequest.expectedOutputs().size())
                    ? batchRequest.expectedOutputs().get(i)
                    : "";
            requests.add(CreateSubmissionRequest.builder()
                    .sourceCode(batchRequest.sourceCode())
                    .languageId(batchRequest.languageId())
                    .stdin(batchRequest.inputs().get(i))
                    .expectedOutput(expected)
                    .build());
        }

        return requests;
    }


    private Judge0BatchResponse sendBatchRequestToJudge0(Judge0BatchRequest request) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Judge0BatchRequest> httpEntity = new HttpEntity<>(request, headers);

            String url = "/submissions/batch?base64_encoded=" + request.base64Encoded();

            ResponseEntity<Judge0SubmissionResponse[]> response = judge0RestTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    httpEntity,
                    Judge0SubmissionResponse[].class
            );

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new ResponseStatusException(
                        HttpStatus.valueOf(response.getStatusCode().value()),
                        "Judge0 batch API error"
                );
            }

            Judge0SubmissionResponse[] responseArray = response.getBody();
            if (responseArray == null) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Empty batch response from Judge0");
            }

            return new Judge0BatchResponse(Arrays.asList(responseArray));

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            log.error("Judge0 API error: {}", e.getResponseBodyAsString());
            throw new ResponseStatusException(
                    HttpStatus.valueOf(e.getStatusCode().value()),
                    "Judge0 API error: " + e.getResponseBodyAsString()
            );
        }
    }

    private Judge0BatchResponse saveSubmissionsToDatabase(Judge0BatchResponse response, String languageId) {
        List<Submission> submissions = response.submissions().stream()
                .map(judgeResponse -> mapper.fromJudge0ResponseToEntity(judgeResponse, languageId))
                .collect(Collectors.toList());

        submissionRepository.saveAll(submissions);
        return response;
    }

    @Override
    public SubmissionResult getSubmissionByToken(String token) {
        log.debug("Getting submission status for token: {}", token);

        try {
            ResponseEntity<Judge0SubmissionResponse> response = judge0RestTemplate.getForEntity(
                    "/submissions/" + token,
                    Judge0SubmissionResponse.class
            );

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new ResponseStatusException(
                        HttpStatus.valueOf(response.getStatusCode().value()),
                        "Judge0 API error"
                );
            }

            Judge0SubmissionResponse judge0Response = response.getBody();
            if (judge0Response == null) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Empty response from Judge0");
            }

            SubmissionResult result = updateSubmissionFromResponse(judge0Response);
            log.debug("Retrieved submission: {} - {}", token, result.status().description());
            return result;

        } catch (HttpClientErrorException e) {
            log.error("Judge0 client error getting submission: {}", token, e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Judge0 error: " + e.getResponseBodyAsString());
        } catch (HttpServerErrorException e) {
            log.error("Judge0 server error getting submission: {}", token, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Judge0 server error");
        } catch (Exception e) {
            log.error("Error getting submission: {}", token, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to get submission");
        }
    }

    @Override
    public Judge0BatchResponse getBatchSubmissions(String tokens, boolean base64Encoded, String fields) {
        log.info("Getting batch submissions for tokens: {}", tokens);

        try {
            String url = "/submissions/batch?tokens=" + tokens +
                    "&base64_encoded=" + base64Encoded +
                    "&fields=" + fields;

            ResponseEntity<Judge0BatchResponse> response = judge0RestTemplate.getForEntity(
                    url,
                    Judge0BatchResponse.class
            );

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new ResponseStatusException(
                        HttpStatus.valueOf(response.getStatusCode().value()),
                        "Judge0 batch API error"
                );
            }

            Judge0BatchResponse batchResponse = response.getBody();
            if (batchResponse == null) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Empty batch response from Judge0");
            }

            log.info("Retrieved {} submissions", batchResponse.submissions().size());
            return batchResponse;

        } catch (HttpClientErrorException e) {
            log.error("Judge0 client error getting batch submissions: {}", tokens, e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Judge0 error: " + e.getResponseBodyAsString());
        } catch (HttpServerErrorException e) {
            log.error("Judge0 server error getting batch submissions: {}", tokens, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Judge0 server error");
        } catch (Exception e) {
            log.error("Error getting batch submissions for tokens: {}", tokens, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to get batch submissions");
        }
    }

    @Override
    public SubmissionResult pollSubmissionUntilComplete(String token) {
        return pollSubmissionUntilComplete(token, 30, 1000); // 30 attempts, 1 second interval
    }

    @Override
    public SubmissionResult pollSubmissionUntilComplete(String token, int maxAttempts, long intervalMs) {
        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            try {
                SubmissionResult result = getSubmissionByToken(token);
                Integer statusId = result.status().id();

                if (statusId != null && statusId != 1 && statusId != 2) {
                    return result; // Completed (not in queue or processing)
                }

                if (attempt < maxAttempts - 1) {
                    Thread.sleep(intervalMs);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Polling interrupted", e);
            } catch (Exception e) {
                if (attempt == maxAttempts - 1) {
                    // Last attempt failed, try fallback to DB
                    return findSubmissionByToken(token);
                }
                log.warn("Attempt {} failed for token {}, retrying...", attempt + 1, token);
            }
        }

        // All attempts exhausted, try fallback to DB
        return findSubmissionByToken(token);
    }

    @Override
    public SubmissionResult findSubmissionByToken(String token) {
        Submission submission = submissionRepository.findByToken(token)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Submission not found: " + token));
        return mapper.fromEntityToResult(submission);
    }

    private Submission saveInitialSubmission(CreateSubmissionRequest request, String token) {
        Submission submission = Submission.builder()
                .token(token)
                .languageId(request.languageId())
                .status("In Queue")
                .statusId(1)
                .build();
        return submissionRepository.save(submission);
    }

    private SubmissionResult updateSubmissionFromResponse(Judge0SubmissionResponse response) {
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
        } else {
            submission = mapper.fromJudge0ResponseToEntity(response);
        }

        submission = submissionRepository.save(submission);
        return mapper.fromEntityToResult(submission);
    }
}