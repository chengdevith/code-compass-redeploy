package kh.edu.istad.codecompass.service.impl;

import jakarta.transaction.Transactional;
import kh.edu.istad.codecompass.domain.*;
import kh.edu.istad.codecompass.dto.TestCaseRequest;
import kh.edu.istad.codecompass.dto.problem.CreateProblemRequest;
import kh.edu.istad.codecompass.dto.problem.ProblemResponse;
import kh.edu.istad.codecompass.mapper.ProblemMapper;
import kh.edu.istad.codecompass.repository.ProblemRepository;
import kh.edu.istad.codecompass.repository.TagRepository;
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

        log.info(problemRequest.testCases().toString());


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
        log.info(testCases.toString());


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
    public ProblemResponse getProblem(long problemId) {

        Problem problem = problemRepository.findProblemByIdAndIsVerifiedTrue(problemId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND ,"Problem not found")
        );

        return problemMapper.fromEntityToResponse(problem);
    }
}
