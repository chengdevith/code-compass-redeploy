package kh.edu.istad.codecompass.service.impl;

import kh.edu.istad.codecompass.domain.SubmissionHistories;
import kh.edu.istad.codecompass.dto.solution.SolutionRequest;
import kh.edu.istad.codecompass.dto.solution.SolutionResponse;
import kh.edu.istad.codecompass.repository.SolutionRepository;
import kh.edu.istad.codecompass.repository.SubmissionHistoryRepository;
import kh.edu.istad.codecompass.repository.UserRepository;
import kh.edu.istad.codecompass.service.SolutionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class SolutionServiceImpl implements SolutionService {

    private final SolutionRepository solutionRepository;
    private final SubmissionHistoryRepository submissionHistoryRepository;
    private final UserRepository userRepository;

    @Override
    public SolutionResponse postSolution(SolutionRequest request, String author) {

        SubmissionHistories submissionHistories = submissionHistoryRepository.findByProblemIdAndUser_Username(request.problemId(), author);

//      now user can post the solution because the problem that the user have solved has status accepted
        if (submissionHistories.getStatus().equals("Accepted")) {
        }

        return null;
    }
}
