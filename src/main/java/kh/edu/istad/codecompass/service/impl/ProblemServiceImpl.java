package kh.edu.istad.codecompass.service.impl;

import jakarta.transaction.Transactional;
import kh.edu.istad.codecompass.domain.*;
import kh.edu.istad.codecompass.dto.testCase.TestCaseRequest;
import kh.edu.istad.codecompass.dto.testCase.TestCaseResponse;
import kh.edu.istad.codecompass.dto.hint.UserHintResponse;
import kh.edu.istad.codecompass.dto.problem.CreateProblemRequest;
import kh.edu.istad.codecompass.dto.problem.ProblemResponse;
import kh.edu.istad.codecompass.dto.problem.ProblemResponseBySpecificUser;
import kh.edu.istad.codecompass.mapper.ProblemMapper;
import kh.edu.istad.codecompass.repository.ProblemRepository;
import kh.edu.istad.codecompass.repository.TagRepository;
import kh.edu.istad.codecompass.repository.UserHintRepository;
import kh.edu.istad.codecompass.repository.UserRepository;
import kh.edu.istad.codecompass.service.ProblemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProblemServiceImpl implements ProblemService {

    private final ProblemRepository problemRepository;
    private final UserRepository userRepository;
    private final ProblemMapper problemMapper;
    private final TagRepository tagRepository;
    private final UserHintRepository userHintRepository;

    @Override
    public ProblemResponse createProblem(CreateProblemRequest problemRequest, String username) {

        if (problemRepository.existsProblemByTitle(problemRequest.title()))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Problem already exists");

        User author = userRepository.findUserByUsername(username).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
        );

        Problem problem = problemMapper.fromRequestToEntity(problemRequest);
        problem.setAuthor(author);

        Problem finalProblem1 = problem;
        List<Hint> hints = problemRequest.hints() == null ?
                List.of()
                :
                problemRequest.hints()
                .stream()
                .map(hintRequest -> {
                    Hint hint = new Hint();
                    hint.setDescription(hintRequest.hint());
                    hint.setIsLocked(hintRequest.isLocked());
                    hint.setProblem(finalProblem1);
                    return hint;
                }).toList();

        problem.setHints(hints);

        // Map test cases
        Problem finalProblem = problem;
        List<TestCase> testCases = (problemRequest.testCases() == null ? List.<TestCaseRequest>of() : problemRequest.testCases())
                .stream()
                .map(tc -> {
                    TestCase testCase = new TestCase();
                    testCase.setInput(tc.input());
                    testCase.setExpectedOutput(tc.expectedOutput());
                    testCase.setProblem(finalProblem);
                    return testCase;
                })
                .toList();

        problem.setTestCases(testCases);

        // Map tags
        Set<Tag> tags = problemRequest.tagNames().stream()
                .map(name -> tagRepository.findByTagName(name)
                        .orElseGet(() -> tagRepository.save(new Tag(name)))) // save if not exist
                .collect(Collectors.toSet());

        problem.setTags(tags);

        problem.setBestMemoryUsage(problemRequest.bestMemoryUsage());
        problem.setBestTimeExecution(problemRequest.bestTimeExecution());
        problem.setIsVerified(false);
        problem.setIsDeleted(false);

        problem = problemRepository.save(problem);

        return problemMapper.fromEntityToResponse(problem);
    }

    @Transactional
    @Override
    public ProblemResponseBySpecificUser getProblemBySpecificUser(String username, long problemId) {

        // Get User
        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // Get Problem
        Problem problem = problemRepository.findById(problemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Problem not found"));

        //  Get UserHint for this user & problem
        List<UserHint> userHints = userHintRepository.findByUserAndHintProblem(user, problem);

//        Map hint to response
        List<UserHintResponse> hintResponses = problem.getHints().stream()
                .map(hint -> {
                    Boolean unlocked = userHints.stream()
                            .filter(uh -> uh.getHint().getId().equals(hint.getId()))
                            .findFirst()
                            .map(UserHint::getIsUnlocked)
                            .orElse(false);
                    return new UserHintResponse(hint.getDescription(), !unlocked); // isLocked = !unlocked
                })
                .collect(Collectors.toList());

        // Map tags
        List<String> tagNames = problem.getTags().stream()
                .map(Tag::getTagName)
                .toList();

        List<TestCaseResponse> testCaseResponses = problem.getTestCases().stream()
                .map(tc -> new TestCaseResponse(tc.getInput(), tc.getExpectedOutput()))
                .toList();

        return new ProblemResponseBySpecificUser(
                problem.getId(),
                problem.getBestMemoryUsage(),
                problem.getBestTimeExecution(),
                problem.getCoin().byteValue(),
                problem.getDescription(),
                problem.getDifficulty(),
                problem.getStar(),
                problem.getTitle(),
                testCaseResponses,
                tagNames,
                hintResponses,
                problem.getAuthor().getUsername()
        );
    }


    @Transactional
    @Override
    public ProblemResponse getProblem(long problemId) {

        Problem problem = problemRepository.findProblemByIdAndIsVerifiedTrue(problemId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND ,"Problem not found")
        );

        return problemMapper.fromEntityToResponse(problem);
    }

        @Transactional
        @Override
        public List<ProblemResponse> getProblems() {
            return problemRepository
                    .findAll()
                    .stream().map(problemMapper::fromEntityToResponse).toList();
        }

    @Transactional
    @Override
    public List<ProblemResponse> getUnverifiedProblems() {
        return problemRepository.findProblemsByIsVerifiedFalse()
                .stream()
                .map(problemMapper::fromEntityToResponse)
                .toList();
    }

    @Transactional
    @Override
    public List<ProblemResponse> getVerifiedProblems() {
        return problemRepository.findProblemsByIsVerifiedTrue()
                .stream()
                .map(problemMapper::fromEntityToResponse)
                .toList();
    }

    @Transactional
    @Override
    public void verifyProblem(long problemId, boolean isVerified) {

        Problem problem  = problemRepository.findProblemByIdAndIsVerifiedFalse(problemId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND ,"Problem not found")
        );
        problem.setIsVerified(isVerified);

        problemRepository.save(problem);
    }


}
