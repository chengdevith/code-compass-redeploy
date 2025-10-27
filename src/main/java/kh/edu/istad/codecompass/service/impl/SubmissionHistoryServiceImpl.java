package kh.edu.istad.codecompass.service.impl;

import kh.edu.istad.codecompass.domain.Problem;
import kh.edu.istad.codecompass.domain.SubmissionHistories;
import kh.edu.istad.codecompass.domain.User;
import kh.edu.istad.codecompass.dto.jugde0.response.Judge0SubmissionResponse;
import kh.edu.istad.codecompass.dto.jugde0.response.SubmissionResult;
import kh.edu.istad.codecompass.dto.submissionHistory.response.SubmissionHistoryResponse;
import kh.edu.istad.codecompass.repository.ProblemRepository;
import kh.edu.istad.codecompass.repository.SubmissionHistoryRepository;
import kh.edu.istad.codecompass.repository.UserRepository;
import kh.edu.istad.codecompass.service.SubmissionHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubmissionHistoryServiceImpl implements SubmissionHistoryService {

    private final SubmissionHistoryRepository submissionHistoryRepository;
    private final ProblemRepository problemRepository;
    private final UserRepository userRepository;

    @Override
    public List<SubmissionHistoryResponse> getAllHistory(String username, Long problemId) {
        return submissionHistoryRepository.findByProblemIdAndUser_Username(problemId ,username)
                .stream()
                .map(submissionHistory -> SubmissionHistoryResponse.builder()
                        .star(submissionHistory.getStar())
                        .coin(submissionHistory.getCoin())
                        .memory(submissionHistory.getMemory())
                        .time(submissionHistory.getTime())
                        .sourceCode(submissionHistory.getCode())
                        .submittedAt(submissionHistory.getSubmittedAt())
                        .languageId(submissionHistory.getLanguageId())
                        .status(submissionHistory.getStatus())
                        .build())
                .toList();
    }

    @Override
    public SubmissionHistoryResponse getLatestWithAccepted(String username, Long problemId) {
        userRepository.findUserByUsername(username).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
        );

        problemRepository.findById(problemId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Problem not found")
        );

        SubmissionHistories submission = submissionHistoryRepository
                .findFirstByUser_UsernameAndProblem_IdAndStatusOrderBySubmittedAtDesc(
                        username,
                        problemId,
                        "Accepted"
                )
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "No accepted submission found")
                );

        // 4️⃣  Map to response
        return SubmissionHistoryResponse.builder()
                .coin(submission.getCoin())
                .languageId(submission.getLanguageId())
                .star(submission.getStar())
                .status(submission.getStatus())
                .sourceCode(submission.getCode())
                .submittedAt(submission.getSubmittedAt())
                .time(submission.getTime())
                .memory(submission.getMemory())
                .build();
    }

}
