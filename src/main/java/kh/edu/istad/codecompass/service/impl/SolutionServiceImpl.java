package kh.edu.istad.codecompass.service.impl;

import jakarta.transaction.Transactional;
import kh.edu.istad.codecompass.domain.Solution;
import kh.edu.istad.codecompass.domain.SubmissionHistories;
import kh.edu.istad.codecompass.domain.User;
import kh.edu.istad.codecompass.domain.UserProblem;
import kh.edu.istad.codecompass.dto.solution.SolutionRequest;
import kh.edu.istad.codecompass.dto.solution.SolutionResponse;
import kh.edu.istad.codecompass.mapper.SolutionMapper;
import kh.edu.istad.codecompass.repository.SolutionRepository;
import kh.edu.istad.codecompass.repository.SubmissionHistoryRepository;
import kh.edu.istad.codecompass.repository.UserProblemRepository;
import kh.edu.istad.codecompass.repository.UserRepository;
import kh.edu.istad.codecompass.service.SolutionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class SolutionServiceImpl implements SolutionService {

    private final SolutionRepository solutionRepository;
    private final SubmissionHistoryRepository submissionHistoryRepository;
    private final UserRepository userRepository;
    private final SolutionMapper solutionMapper;
    private final UserProblemRepository userProblemRepository;

    @Transactional
    @Override
    public SolutionResponse postSolution(SolutionRequest request, String author) {

        User user = userRepository.findUserByUsername(author).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found")
        );
        if (user.getIsDeleted().equals(true))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found");

        UserProblem userProblem = userProblemRepository.findByUserIdAndProblemId(user.getId(), request.problemId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.FORBIDDEN, "To post the solution, you must solve the problem first")
        );

        Solution solution = new Solution();
        solution.setExplanation(request.explanation());
        solution.setUser(user);
        solution.setSourceCode(request.sourceCode());
        solution.setIsDeleted(false);
        solution.setProblem(userProblem.getProblem());
        solution.setLanguageId(request.languageId());
        solution.setTags(request.tags());
        solution.setTitle(request.title());

        solution = solutionRepository.save(solution);

        return solutionMapper.toResponse(solution);
    }

    @Transactional
    @Override
    public List<SolutionResponse> getAllSolutions(String username, Long problemId) {

        boolean isUserSolved = false;
        List<SubmissionHistories>  submissionHistories = submissionHistoryRepository.findByProblemIdAndUser_Username(problemId, username);

        for  (SubmissionHistories history : submissionHistories) {
            if (history.getStatus().equals("Accepted")) {
                isUserSolved = true;
                break;
            }
        }

        if (!isUserSolved)
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "To view the solutions, you must solve the problem first");

        return solutionRepository.findSolutionByProblemIdAndIsDeletedFalse(problemId).stream().map(
            solutionMapper::toResponse
        ).toList();
    }

    @Override
    public void deleteSolution(Long solutionId, String author) {
        Solution solution = solutionRepository.findSolutionByIdAndUser_Username(solutionId,  author).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Solution not found")
        );
        solution.setIsDeleted(true);
        solutionRepository.save(solution);
    }

//    @Override
//    public void lkeSolution(Long solutionId, String username) {
//        Solution solution = solutionRepository.findById(solutionId).orElseThrow(
//                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Solution not found")
//        );
//
//        solution.setLikeCount(solution.getLikeCount() == null ? 1 : solution.getLikeCount() + 1);
//        solutionRepository.save(solution);
//    }

}
