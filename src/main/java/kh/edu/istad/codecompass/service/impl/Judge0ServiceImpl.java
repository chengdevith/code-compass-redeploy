package kh.edu.istad.codecompass.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import kh.edu.istad.codecompass.config.Judge0TimeoutConfig;
import kh.edu.istad.codecompass.domain.*;
import kh.edu.istad.codecompass.domain.Package;
import kh.edu.istad.codecompass.dto.jugde0.request.BatchSubmissionRequest;
import kh.edu.istad.codecompass.dto.jugde0.request.CreateSubmissionRequest;
import kh.edu.istad.codecompass.dto.jugde0.request.Judge0BatchRequest;
import kh.edu.istad.codecompass.dto.jugde0.response.Judge0BatchResponse;
import kh.edu.istad.codecompass.dto.jugde0.response.Judge0SubmissionResponse;
import kh.edu.istad.codecompass.dto.jugde0.response.SubmissionResult;
import kh.edu.istad.codecompass.enums.Star;
import kh.edu.istad.codecompass.mapper.Judge0Mapper;
import kh.edu.istad.codecompass.repository.*;
import kh.edu.istad.codecompass.service.Judge0Service;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import java.text.MessageFormat;
import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class Judge0ServiceImpl implements Judge0Service {

    private final Judge0TimeoutConfig judge0TimeoutConfig;
    private final WebClient judge0WebClient;
    private final Judge0Mapper judge0Mapper;
    private final SubmissionHistoryRepository submissionHistoryRepository;
    private final UserRepository userRepository;
    private final ProblemRepository problemRepository;
    private final UserProblemRepository userProblemRepository;
    private final PackageRepository packageRepository;
    private final BadgeRepository badgeRepository;
    private final LeaderBoardRepository leaderBoardRepository;

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
    public Judge0SubmissionResponse getSubmissionByToken(String token) {
        return judge0WebClient.get()
                .uri("/submissions/{token}", token)
                .retrieve()
                .bodyToMono(Judge0SubmissionResponse.class)
                .block();
    }


    @Transactional
    @Override
    public Judge0BatchResponse createSubmissionBatch(BatchSubmissionRequest batchRequest, String username, Long problemId) {

        List<CreateSubmissionRequest> submissions = prepareSubmissionRequests(batchRequest);
        Judge0BatchRequest request = new Judge0BatchRequest(submissions);
        Judge0BatchResponse responses = sendBatchRequestToJudge0(request, batchRequest.languageId());

        User user = userRepository.findUserByUsername(username).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found")
        );
        if (user.getIsDeleted().equals(true))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found");

        Problem problem = problemRepository.findProblemByIdAndIsVerifiedTrue(problemId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Problem not found")
        );

        AtomicInteger counter = new AtomicInteger(0);
        AtomicInteger userMemoryUsage = new AtomicInteger(0);
        AtomicReference<Double> userExecutionTime = new AtomicReference<>(0.0);

        String overallStatus = determineOverallStatus(responses.submissions());

        // Calculate metrics only for accepted submissions
        responses.submissions().forEach(submission -> {
            if (submission.status().description().equals("Accepted")) {
                counter.incrementAndGet();
                userMemoryUsage.updateAndGet(u -> u + submission.memory());
                userExecutionTime.updateAndGet(t -> t + Double.parseDouble(submission.time()));
            }
        });

        // Create submission history
        SubmissionHistories submissionHistories = new SubmissionHistories();
        submissionHistories.setProblem(problem);
        submissionHistories.setUser(user);
        submissionHistories.setCode(batchRequest.sourceCode());
        submissionHistories.setStatus(overallStatus);
        submissionHistories.setLanguageId(batchRequest.languageId());
        submissionHistories.setSubmittedAt(LocalDateTime.now());
        submissionHistories.setTime(userExecutionTime.toString());
        submissionHistories.setMemory(userMemoryUsage.get());

        Double peekTimeExecution = problem.getBestTimeExecution();
        Integer peekMemoryUsage = problem.getBestMemoryUsage();

        // Only process rewards if ALL submissions are accepted
        if (overallStatus.equals("Accepted")) {
            int earnedStars = 1; // Base star for solving
            double averageUserMemoryUsage = (double) userMemoryUsage.get() / counter.get();
            double averageUserExecutionTime = userExecutionTime.get() / counter.get();

            if (averageUserExecutionTime <= peekTimeExecution)
                earnedStars++;

            if (averageUserMemoryUsage <= peekMemoryUsage)
                earnedStars++;

            earnedStars = Math.min(earnedStars, 3);

            int baseCoins = problem.getCoin();
            double earnedCoins = baseCoins / 4.0;   // Base reward for solving

            // Time efficiency reward
            if (averageUserExecutionTime <= peekTimeExecution * 1.5)
                earnedCoins += baseCoins / 4.0;
            else if (averageUserExecutionTime <= peekTimeExecution * 2)
                earnedCoins += baseCoins / 6.0;

            // Memory efficiency reward
            if (averageUserMemoryUsage <= peekMemoryUsage * 1.5)
                earnedCoins += baseCoins / 4.0;
            else if (averageUserMemoryUsage <= peekMemoryUsage * 2)
                earnedCoins += baseCoins / 6.0;

            long roundedCoins = Math.round(earnedCoins);
            earnedCoins = Math.min(roundedCoins, problem.getCoin());

            boolean hasSolvedBefore = submissionHistoryRepository
                    .findByProblemIdAndUser_Username(problemId, username)
                    .stream()
                    .anyMatch(history -> history.getStatus().equals("Accepted"));

            // Set stars and coins for the submission history
            Star star = switch (earnedStars) {
                case 1 -> Star.ONE;
                case 2 -> Star.TWO;
                case 3 -> Star.THREE;
                default -> Star.ZERO;
            };
            submissionHistories.setStar(star);
            submissionHistories.setCoin((int) earnedCoins);

            // User has solved this specific problem for the first time
            if (!hasSolvedBefore) {
                user.setCoin(user.getCoin() + (int) earnedCoins);
                user.setStar(user.getStar() + earnedStars);
                user.updateLevel();
                user.setTotalProblemsSolved(user.getTotalProblemsSolved() + 1);

                UserProblem userProblem = new UserProblem();
                userProblem.setUser(user);
                userProblem.setProblem(problem);
                userProblem.setIsSolved(true);
                userProblemRepository.save(userProblem);

                List<UserProblem> userProblemList = userProblemRepository
                        .findAllByUserIdAndIsSolvedTrue(user.getId());

                // Collect all solved problem IDs
                Set<Long> solvedProblemIds = userProblemList.stream()
                        .map(u -> u.getProblem().getId())
                        .collect(Collectors.toSet());

                List<Package> packageList = packageRepository.findPackagesByProblems_Id(problemId);

                for (Package pack : packageList) {
                    Set<Long> packageProblemIds = pack.getProblems().stream()
                            .map(Problem::getId)
                            .collect(Collectors.toSet());

                    // Check if user solved all problems in the package
                    boolean solvedAll = solvedProblemIds.containsAll(packageProblemIds);
                    if (solvedAll) {
                        Badge badge = badgeRepository.findBadgesByProblemPackage_Name(pack.getName())
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Badge not found"));

                        if (!user.getBadges().contains(badge))
                            user.getBadges().add(badge);
                    }
                }

                // Leaderboard logic
                LeaderBoard leaderBoard = leaderBoardRepository.findById(1L)
                        .orElseGet(LeaderBoard::new);
                user.setLeaderBoard(leaderBoard);
                leaderBoard.getUsers().add(user);
                leaderBoardRepository.save(leaderBoard);

            } else {
                // User has solved before - only update if better performance
                List<SubmissionHistories> submissionHistoriesList =
                        submissionHistoryRepository.findByProblemIdAndUser_Username(problemId, username);

                int maxHistoryCoins = submissionHistoriesList.stream()
                        .mapToInt(history -> history.getCoin() == null ? 0 : history.getCoin())
                        .max()
                        .orElse(0);

                Star maxHistoryStars = submissionHistoriesList.stream()
                        .map(history -> history.getStar() == null ? Star.ZERO : history.getStar())
                        .max(Enum::compareTo)
                        .orElse(Star.ZERO);

                // Coins: only add if earned > history
                if (earnedCoins > maxHistoryCoins) {
                    user.setCoin(user.getCoin() + (int) (earnedCoins - maxHistoryCoins));
                }

                // Stars: only add if earned > history
                Star currentStar = star; // Use the star that's already calculated
                if (currentStar.compareTo(maxHistoryStars) > 0) {
                    int additionalStars = currentStar.ordinal() - maxHistoryStars.ordinal();
                    user.setStar(user.getStar() + additionalStars);
                }
                user.updateLevel();
            }

            userRepository.save(user);
        }

        // Save submission history (for both accepted and failed cases)
        submissionHistoryRepository.save(submissionHistories);

        // Update ranks after all user changes
        updateUserRanks();

        return responses;
    }

    @Transactional
    @Override
    public Judge0BatchResponse runSubmissionBatch(BatchSubmissionRequest batchRequest) {

        List<CreateSubmissionRequest> submissions = prepareSubmissionRequests(batchRequest);
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
                    .cpuTimeLimit(judge0TimeoutConfig.getCpuTimeLimit())
                    .wallTimeLimit(judge0TimeoutConfig.getWallTimeLimit())
                    .memoryLimit(judge0TimeoutConfig.getMemoryLimit())
                    .build());
        }
        return requests;
    }

    @Transactional
    protected Judge0BatchResponse sendBatchRequestToJudge0(Judge0BatchRequest request, String languageId) {
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

            if (responses == null || responses.length == 0)
                throw new RuntimeException("No response received from Judge0");


            log.info("Received {} responses from Judge0", responses.length);

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

    @Transactional
    protected Judge0BatchResponse pollForResults(Judge0SubmissionResponse[] tokenResponses, String languageId) {
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

    @Transactional
    protected Judge0SubmissionResponse pollSingleSubmission(String token) {
        int maxAttempts = judge0TimeoutConfig.getMaxPollingAttempts();
        int attempt = 0;

        log.info("Polling submission with token: {}", token);

        while (attempt < maxAttempts) {
            try {
                // Add more detailed logging for the request
                String url = "/submissions/" + token;
                log.debug("Polling attempt {} for token {} using URL: {}", attempt + 1, token, url);

                Judge0SubmissionResponse response = judge0WebClient.get()
                        .uri("/submissions/{token}", token)
                        .retrieve()
                        .onStatus(HttpStatusCode::isError, clientResponse -> {
                            log.error("Error polling token {}: {} - attempting to get error body",
                                    token, clientResponse.statusCode());
                            return clientResponse.bodyToMono(String.class)
                                    .doOnNext(errorBody -> log.error("Error body for token {}: {}", token, errorBody))
                                    .map(errorBody -> new RuntimeException(
                                            MessageFormat.format("Failed to poll submission {0}: {1} - {2}",
                                                    token, clientResponse.statusCode(), errorBody)));
                        })
                        .bodyToMono(Judge0SubmissionResponse.class)
                        .timeout(judge0TimeoutConfig.getPollingTimeout())
                        .block();

                if (response != null) {
                    log.debug("Attempt {}: Token {}, Status: {} (ID: {})",
                            attempt + 1, token,
                            response.status() != null ? response.status().description() : "null",
                            response.status() != null ? response.status().id() : "null");

                    if (response.status() != null && response.status().id() != null) {
                        // Status ID 1 = In Queue, 2 = Processing
                        if (response.status().id() > 2) {
                            log.info("Submission {} completed with status: {} ({})",
                                    token, response.status().id(), response.status().description());
                            return response;
                        }
                    } else {
                        log.warn("Received response with null status for token: {}", token);
                    }
                } else {
                    log.warn("Received null response for token: {}", token);
                }

                attempt++;

                // Exponential backoff: start with 1s, then 2s, 3s, max 5s
                int sleepTime = Math.min(1000 + (attempt * 500), 5000);
                log.debug("Waiting {}ms before next poll attempt for token: {}", sleepTime, token);
                Thread.sleep(sleepTime);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Polling interrupted for token: {}", token);
                return createTimeoutResponse(token);
            } catch (Exception e) {
                log.error("Error polling submission {} (attempt {}): {}", token, attempt + 1, e.getMessage());

                attempt++;

                if (attempt < maxAttempts) {
                    try {
                        int sleepTime = Math.min(1000 + (attempt * 200), 3000);
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }

        log.warn("Timeout polling submission after {} attempts: {}", maxAttempts, token);
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

    @Transactional
    protected Judge0BatchResponse mapResponses(Judge0SubmissionResponse[] responses, String languageId) {
        List<Judge0SubmissionResponse> mappedResponses = new ArrayList<>();

        for (Judge0SubmissionResponse resp : responses) {
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


    // Helper method to determine overall status
    private String determineOverallStatus(List<Judge0SubmissionResponse> submissions) {
        // If any submission is not accepted, return the first non-accepted status
        return submissions.stream()
                .filter(sub -> !sub.status().description().equals("Accepted"))
                .findFirst()
                .map(sub -> sub.status().description())
                .orElse("Accepted");
    }

    private void updateUserRanks() {
        List<User> users = userRepository.findAllByOrderByStarDesc();
        long rank = 1;
        Integer prevStar = -1;
        long sameRank = 1;

        for (User u : users) {
            if (!u.getStar().equals(prevStar)) {
                rank = sameRank;
                prevStar = u.getStar();
            }
            u.setRank(rank);
            sameRank++;
        }
        userRepository.saveAll(users);
    }
}