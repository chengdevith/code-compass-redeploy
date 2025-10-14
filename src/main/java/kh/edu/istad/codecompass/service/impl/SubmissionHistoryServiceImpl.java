package kh.edu.istad.codecompass.service.impl;

import kh.edu.istad.codecompass.domain.Problem;
import kh.edu.istad.codecompass.dto.submissionHistory.response.SubmissionHistoryResponse;
import kh.edu.istad.codecompass.repository.ProblemRepository;
import kh.edu.istad.codecompass.repository.SubmissionHistoryRepository;
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

    @Override
    public List<SubmissionHistoryResponse> getAllHistory(String username, Long problemId) {
        return submissionHistoryRepository.findByProblemIdAndUser_Username(problemId ,username)
                .stream()
                .map(submissionHistory -> SubmissionHistoryResponse.builder()
                        .star(submissionHistory.getStar())
                        .coin(submissionHistory.getCoin())
                        .sourceCode(submissionHistory.getCode())
                        .submittedAt(submissionHistory.getSubmittedAt())
                        .languageId(submissionHistory.getLanguageId())
                        .status(submissionHistory.getStatus())
                        .build())
                .toList();
    }
}
