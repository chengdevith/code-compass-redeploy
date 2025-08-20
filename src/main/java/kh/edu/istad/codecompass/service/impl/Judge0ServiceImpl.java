package kh.edu.istad.codecompass.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import kh.edu.istad.codecompass.domain.Submission;
import kh.edu.istad.codecompass.dto.jugde0.*;
import kh.edu.istad.codecompass.mapper.Judge0Mapper;
import kh.edu.istad.codecompass.service.Judge0Service;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.text.MessageFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class Judge0ServiceImpl implements Judge0Service {

    private final WebClient judge0WebClient;
    private final Judge0Mapper judge0Mapper;
//    private final SubmissionRepository submissionRepository;

    @Override
    public SubmissionResult createSubmission(CreateSubmissionRequest request) {
        log.info("Creating submission: {}", request);

        Judge0SubmissionResponse judge0SubmissionResponse = judge0WebClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/submissions")
                        .queryParam("base64_encoded", false)
                        .queryParam("wait", true)
                        .build())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Judge0SubmissionResponse.class)
                .block();

        Submission submission = judge0Mapper.fromJudge0ResponseToEntity(judge0SubmissionResponse);
        submission.setLanguageId(request.languageId());
        return judge0Mapper.fromEntityToResult(submission);
    }

    @Override
    public SubmissionResult getSubmissionByToken(String token) {
        return judge0WebClient.get()
                .uri("/submissions/{token}", token)
                .retrieve()
                .bodyToMono(SubmissionResult.class)
                .block();
    }

    @Override
    public Judge0BatchResponse createSubmissionBatch(BatchSubmissionRequest batchRequest) {
        log.info("Creating batch submissions for language: {}", batchRequest.languageId());

        List<CreateSubmissionRequest> submissions = prepareSubmissionRequests(batchRequest);
        log.info("Creating {} submissions", submissions.size());

        Judge0BatchRequest request = new Judge0BatchRequest(submissions);

        return sendBatchRequestToJudge0(request, batchRequest.languageId());
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
                    .cpuTimeLimit(2.0)
                    .wallTimeLimit(5.0)
                    .memoryLimit(128000)
                    .build());
        }
        return requests;
    }

    private Judge0BatchResponse sendBatchRequestToJudge0(Judge0BatchRequest request, String languageId) {
        try {
            log.info("Sending batch request to Judge0 with {} submissions", request.submissions().size());

            // Log the raw JSON payload
            ObjectMapper objectMapper = new ObjectMapper();
            String requestJson = objectMapper.writeValueAsString(request);
            log.info("Judge0 batch request payload: {}", requestJson);

            Judge0SubmissionResponse[] responses = judge0WebClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/submissions/batch")
                            .queryParam("base64_encoded", false)
                            .queryParam("wait", true)
                            .build())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, clientResponse -> {
                        log.error("Judge0 API error: {}", clientResponse.statusCode());
                        return clientResponse.bodyToMono(String.class)
                                .map(body -> new RuntimeException(MessageFormat.format("Judge0 API error: {0}", body)));
                    })
                    .bodyToMono(Judge0SubmissionResponse[].class)
                    .timeout(Duration.ofSeconds(30))
                    .block();

            if (responses == null || responses.length == 0) {
                throw new RuntimeException("No response received from Judge0");
            }

            log.info("Received {} responses from Judge0", responses.length);

            // Log first response for debugging
            Judge0SubmissionResponse firstResponse = responses[0];
            log.info("First response - Token: {}, Status: {}, Stdout: {}, Language: {}",
                    firstResponse.token(),
                    firstResponse.status(),
                    firstResponse.stdout(),
                    firstResponse.languageId());

//            for (int i = 0; i < responses.length; i++) {
//                responses[i].languageId()
//            }

            // Check if we got actual results or just tokens
            boolean hasResults = Arrays.stream(responses)
                    .anyMatch(r -> r.status() != null || r.stdout() != null || r.stderr() != null);

            if (!hasResults) {
                log.info("Got tokens only, polling for results...");
                return pollForResults(responses, languageId);
            }

            return mapResponses(responses, languageId);

        } catch (Exception e) {
            log.error("Error in batch submission: {}", e.getMessage(), e);
            throw new RuntimeException(MessageFormat.format("Failed to execute batch submissions: {0}", e.getMessage()), e);
        }
    }

    private Judge0BatchResponse pollForResults(Judge0SubmissionResponse[] tokenResponses, String languageId) {
        List<Judge0SubmissionResponse> results = new ArrayList<>();

        for (Judge0SubmissionResponse tokenResponse : tokenResponses) {
            if (tokenResponse.token() == null) {
                log.error("No token received for submission");
                results.add(createErrorResponse());
                continue;
            }

            Judge0SubmissionResponse result = pollSingleSubmission(tokenResponse.token());
            results.add(new Judge0SubmissionResponse(
                    languageId,
                    result.stdout(),
                    result.time(),
                    result.memory(),
                    result.stderr(),
                    result.token(),
                    result.compileOutput(),
                    result.message(),
                    result.status()
            ));
        }

        return new Judge0BatchResponse(results);
    }

    private Judge0SubmissionResponse pollSingleSubmission(String token) {
        int maxAttempts = 15;
        int attempt = 0;

        log.info("Polling submission with token: {}", token);

        while (attempt < maxAttempts) {
            try {
                Judge0SubmissionResponse response = judge0WebClient.get()
                        .uri("/submissions/{token}", token)
                        .retrieve()
                        .onStatus(HttpStatusCode::isError, clientResponse -> {
                            log.error("Error polling token {}: {}", token, clientResponse.statusCode());
                            return Mono.error(new RuntimeException("Failed to poll submission"));
                        })
                        .bodyToMono(Judge0SubmissionResponse.class)
                        .timeout(Duration.ofSeconds(10))
                        .block();

                if (response != null) {
                    log.debug("Attempt {}: Token {}, Status: {}", attempt + 1, token, response.status());

                    if (response.status() != null && response.status().id() != null) {
                        // Status ID 1 = In Queue, 2 = Processing
                        if (response.status().id() > 2) {
                            log.info("Submission {} completed with status: {} ({})",
                                    token, response.status().id(), response.status().description());
                            return response;
                        }
                    }
                }

                attempt++;
                Thread.sleep(2000); // Wait for 2 seconds before next poll

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Polling interrupted for token: {}", token);
                return createTimeoutResponse(token);
            } catch (Exception e) {
                log.error("Error polling submission {}: {}", token, e.getMessage());
                attempt++;

                if (attempt < maxAttempts) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }

        log.warn("Timeout polling submission: {}", token);
        return createTimeoutResponse(token);
    }

    private Judge0SubmissionResponse createTimeoutResponse(String token) {
        return new Judge0SubmissionResponse(
                null, // languageId
                null, // stdout
                null, // time
                null, // memory
                "Execution timeout during polling", // stderr
                token,
                null, // compileOutput
                "Polling timeout exceeded", // message
                new Judge0SubmissionResponse.Status(13, "Internal Error") // status
        );
    }

    private Judge0SubmissionResponse createErrorResponse() {
        return new Judge0SubmissionResponse(
                null, // languageId
                null, // stdout
                null, // time
                null, // memory
                "No token received", // stderr
                null, // token
                null, // compileOutput
                "No token received", // message
                new Judge0SubmissionResponse.Status(13, "Internal Error") // status
        );
    }

    private Judge0BatchResponse mapResponses(Judge0SubmissionResponse[] responses, String languageId) {
        List<Judge0SubmissionResponse> mappedResponses = new ArrayList<>();

        for (Judge0SubmissionResponse resp : responses) {
            //            String languageId = resp.languageId() == null ?  original.languageId() : resp.languageId();

            mappedResponses.add(new Judge0SubmissionResponse(
                    languageId,
                    resp.stdout(),
                    resp.time(),
                    resp.memory(),
                    resp.stderr(),
                    resp.token(),
                    resp.compileOutput(),
                    resp.message(),
                    resp.status()
            ));
        }

        return new Judge0BatchResponse(mappedResponses);
    }
}

