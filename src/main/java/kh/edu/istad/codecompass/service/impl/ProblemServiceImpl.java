package kh.edu.istad.codecompass.service.impl;

import jakarta.transaction.Transactional;
import kh.edu.istad.codecompass.domain.*;
import kh.edu.istad.codecompass.dto.problem.request.UpdateProblemRequest;
import kh.edu.istad.codecompass.dto.problem.response.ProblemSummaryResponse;
import kh.edu.istad.codecompass.dto.testCase.TestCaseRequest;
import kh.edu.istad.codecompass.dto.testCase.TestCaseResponse;
import kh.edu.istad.codecompass.dto.hint.response.UserHintResponse;
import kh.edu.istad.codecompass.dto.problem.request.CreateProblemRequest;
import kh.edu.istad.codecompass.dto.problem.response.ProblemResponse;
import kh.edu.istad.codecompass.dto.problem.response.ProblemResponseBySpecificUser;
import kh.edu.istad.codecompass.elasticsearch.domain.ProblemIndex;
import kh.edu.istad.codecompass.elasticsearch.repository.ProblemElasticsearchRepository;
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
    private final ProblemElasticsearchRepository problemElasticsearchRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;
    private final UserHintRepository userHintRepository;
    private final ProblemMapper problemMapper;

    @Transactional
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
                    hint.setDescription(hintRequest.description());
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
        ProblemIndex problemIndex = ProblemIndex.builder()
                .description(problem.getDescription())
                .title(problem.getTitle())
                .difficulty(problem.getDifficulty())
                .coin(problem.getCoin())
                .star(problem.getStar())
                .bestTimeExecution(problem.getBestTimeExecution())
                .bestMemoryUsage(problem.getBestMemoryUsage())
                .authorId(problem.getAuthor().getId())
                .authorUsername(problem.getAuthor().getUsername())
                .build();

        problemElasticsearchRepository.save(problemIndex);

        return problemMapper.fromEntityToResponse(problem);
    }

    @Transactional
    @Override
    public ProblemResponseBySpecificUser getProblemBySpecificUser(String username, long problemId) {

        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Problem problem = problemRepository.findProblemByIdAndIsVerifiedTrue(problemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Problem not found"));

        //  Get UserHint for a specific user & problem
        List<UserHint> userHints = userHintRepository.findByUserAndHintProblem(user, problem);

        List<UserHintResponse> hintResponses = problem.getHints().stream()
                .map(hint -> {
                    Boolean unlocked = userHints.stream()
                            .filter(uh -> uh.getHint().getId().equals(hint.getId()))
                            .findFirst()
                            .map(UserHint::getIsUnlocked)
                            .orElse(false);
                    return new UserHintResponse(hint.getId(), hint.getDescription(), !unlocked); // isLocked = !unlocked
                })
                .collect(Collectors.toList());

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
    public List<ProblemSummaryResponse> getProblems() {
        return problemRepository
                .findAll()
                .stream().map(problem ->
                        ProblemSummaryResponse
                                .builder()
                                .id(problem.getId())
                                .difficulty(problem.getDifficulty())
                                .tags(problem.getTags().stream().map(Tag::getTagName).collect(Collectors.toList()))
                                .title(problem.getTitle())
                                .build()).toList();
    }

    @Transactional
    @Override
    public List<ProblemSummaryResponse> getUnverifiedProblems() {
        return problemRepository.findProblemsByIsVerifiedFalse()
                .stream()
                .map(problem ->
                        ProblemSummaryResponse
                                .builder()
                                .id(problem.getId())
                                .difficulty(problem.getDifficulty())
                                .tags(problem.getTags().stream().map(Tag::getTagName).collect(Collectors.toList()))
                                .title(problem.getTitle())
                                .build()).toList();
    }

    @Transactional
    @Override
    public List<ProblemSummaryResponse> getVerifiedProblems() {
        return problemRepository.findProblemsByIsVerifiedTrue()
                .stream()
                .map(problem ->
                        ProblemSummaryResponse
                                .builder()
                                .id(problem.getId())
                                .difficulty(problem.getDifficulty())
                                .tags(problem.getTags().stream().map(Tag::getTagName).collect(Collectors.toList()))
                                .title(problem.getTitle())
                                .build()).toList();
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

    @Transactional
    @Override
    public void updateProblem(Long problemId, String authorUsername, UpdateProblemRequest updateProblemRequest) {

        Problem problem = problemRepository.findProblemByIdAndAuthor_Username(problemId ,authorUsername)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Problem not found"));

        problemMapper.fromUpdateRequestToEntity(updateProblemRequest, problem);

        ProblemIndex problemIndex = ProblemIndex.builder()
                .description(problem.getDescription())
                .title(problem.getTitle())
                .difficulty(problem.getDifficulty())
                .coin(problem.getCoin())
                .star(problem.getStar())
                .bestTimeExecution(problem.getBestTimeExecution())
                .bestMemoryUsage(problem.getBestMemoryUsage())
                .authorId(problem.getAuthor().getId())
                .authorUsername(problem.getAuthor().getUsername())
                .build();

        problemElasticsearchRepository.save(problemIndex);

    }


}
