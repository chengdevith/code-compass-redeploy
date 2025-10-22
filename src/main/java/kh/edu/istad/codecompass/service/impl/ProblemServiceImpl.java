package kh.edu.istad.codecompass.service.impl;

import jakarta.transaction.Transactional;
import kh.edu.istad.codecompass.domain.*;
import kh.edu.istad.codecompass.dto.hint.HintRequest;
import kh.edu.istad.codecompass.dto.problem.request.UpdateProblemRequest;
import kh.edu.istad.codecompass.dto.problem.response.*;
import kh.edu.istad.codecompass.dto.testCase.TestCaseRequest;
import kh.edu.istad.codecompass.dto.testCase.TestCaseResponse;
import kh.edu.istad.codecompass.dto.hint.response.UserHintResponse;
import kh.edu.istad.codecompass.dto.problem.request.CreateProblemRequest;
import kh.edu.istad.codecompass.elasticsearch.domain.ProblemIndex;
import kh.edu.istad.codecompass.elasticsearch.repository.ProblemElasticsearchRepository;
import kh.edu.istad.codecompass.enums.Status;
import kh.edu.istad.codecompass.mapper.ProblemMapper;
import kh.edu.istad.codecompass.repository.*;
import kh.edu.istad.codecompass.service.ProblemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;
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
    private final HintRepository hintRepository;
    private final TestCaseRepository testCaseRepository;
    private final UserProblemRepository userProblemRepository;
    @Transactional
    @Override
    public ProblemResponse createProblem(CreateProblemRequest problemRequest, String username) {

        if (problemRepository.existsProblemByTitleAndIsDeletedFalse(problemRequest.title()))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Problem already exists");

        User author = userRepository.findUserByUsername(username).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found")
        );
        if (author.getIsDeleted().equals(true))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found");

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
        problem.setStatus(Status.PENDING);

        problem = problemRepository.save(problem);

        return problemMapper.fromEntityToResponse(problem);
    }

    @Transactional
    @Override
    public ProblemResponseBySpecificUser getProblemBySpecificUser(String username, long problemId) {

        User user = userRepository.findUserByUsername(username).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found")
        );
        if (user.getIsDeleted().equals(true))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found");

        Problem problem = problemRepository.findProblemByIdAndIsVerifiedTrue(problemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Problem not found"));

        //  Get UserHint for a specific user & problem
        List<UserHint> userHints = userHintRepository.findByUserAndHintProblem(user, problem);

        List<Hint> hints = hintRepository.findByProblem_Id(problemId);

        for (Hint hint : hints) {
            // find existing UserHint for this user
            UserHint userHint = userHints.stream()
                    .filter(uh -> uh.getHint().getId().equals(hint.getId()))
                    .findFirst()
                    .orElseGet(() -> {
                        // create a new UserHint if it doesn't exist
                        UserHint uh = new UserHint();
                        uh.setUser(user);
                        uh.setHint(hint);
                        uh.setIsUnlocked(false);
                        return uh;
                    });

            // unlock if the hint is unlocked by default
            if (!hint.getIsLocked() && !userHint.getIsUnlocked()) {
                userHint.setIsUnlocked(true);
            }

            // save or update
            userHintRepository.save(userHint);

            // add or update in userHints list so it can be used for response mapping
            if (!userHints.contains(userHint)) {
                userHints.add(userHint);
            }
        }

        List<UserHintResponse> hintResponses = hints.stream()
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
                problem.getCoin(),
                problem.getDescription(),
                problem.getDifficulty(),
                problem.getStar(),
                problem.getTitle(),
                testCaseResponses,
                tagNames,
                hintResponses,
                problem.getAuthor().getUsername(),
                problem.getIsDeleted(),
                problem.getIsVerified()

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
                .findByIsDeletedFalse()
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
    public Page<ProblemSummaryResponse> getUnverifiedProblems(Pageable pageable) {

        Page<Problem> problems = problemRepository.findProblemsByIsVerifiedFalseAndIsDeletedFalse(pageable);

        return problems.map(problem -> ProblemSummaryResponse.builder()
                .id(problem.getId())
                .difficulty(problem.getDifficulty())
                .tags(problem.getTags().stream()
                        .map(Tag::getTagName)
                        .collect(Collectors.toList()))
                .title(problem.getTitle())
                .build());
    }

    @Transactional
    @Override
    public Page<ProblemSummaryResponse> getVerifiedProblems(Pageable pageable) {
        Page<Problem> problems = problemRepository.findProblemsByIsVerifiedTrue(pageable);

        return problems.map(problem -> ProblemSummaryResponse.builder()
                .id(problem.getId())
                .difficulty(problem.getDifficulty())
                .tags(problem.getTags().stream()
                        .map(Tag::getTagName)
                        .collect(Collectors.toList()))
                .title(problem.getTitle())
                .build());
    }

    @Transactional
    @Override
    public ProblemResponse verifyProblem(long problemId, boolean isVerified) {

        Problem problem  = problemRepository.findProblemByIdAndIsVerifiedFalseAndIsDeletedFalse(problemId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND ,"Problem not found")
        );
        problem.setIsVerified(isVerified);
        problem.setStatus(Status.APPROVED);

        problem = problemRepository.save(problem);

        ProblemIndex problemIndex = ProblemIndex.builder()
                .problemId(problem.getId())
                .description(problem.getDescription())
                .title(problem.getTitle())
                .difficulty(problem.getDifficulty())
                .coin(problem.getCoin())
                .star(problem.getStar())
                .bestTimeExecution(problem.getBestTimeExecution())
                .bestMemoryUsage(problem.getBestMemoryUsage())
                .authorId(problem.getAuthor().getId())
                .authorUsername(problem.getAuthor().getUsername())
                .tags(problem.getTags().stream()
                        .map(Tag::getTagName)
                        .collect(Collectors.toSet()))
                .build();
        problemElasticsearchRepository.save(problemIndex);


        return problemMapper.fromEntityToResponse(problem);
    }

    @Transactional
    @Override
    public void updateProblem(Long problemId, String authorUsername, UpdateProblemRequest updateProblemRequest) {
        Problem problem = problemRepository.findProblemByIdAndAuthor_UsernameAndIsDeletedFalse(problemId, authorUsername)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Problem not found"));

        log.info("Updating problem: {}", updateProblemRequest);

        // 1. Map simple fields
        problemMapper.fromUpdateRequestToEntity(updateProblemRequest, problem);

        // 2. Update hints - do this BEFORE other operations
        if (updateProblemRequest.hints() != null) {
            updateHints(problem, updateProblemRequest.hints());
        }

        // 3. Update test cases
        if (updateProblemRequest.testCases() != null) {
            updateTestCases(problem, updateProblemRequest.testCases());
        }

        // 4. Update tags
        if (updateProblemRequest.tagNames() != null) {
            updateTags(problem, updateProblemRequest.tagNames());
        }

        // 5. Set timestamp
        problem.setUpdateAt(LocalDateTime.now());

        // 6. Save problem
        problemRepository.save(problem);

        // 7. Update Elasticsearch
        updateProblemIndex(problem);
    }

    private void updateHints(Problem problem, List<HintRequest> hintRequests) {
        log.info("Updating {} hints", hintRequests.size());

        // Remove hints from DB first
        problem.getHints().removeIf(h -> true); // keeps the parent reference intact

        // Add new hints
        for (HintRequest hr : hintRequests) {
            Hint hint = new Hint();
            hint.setDescription(hr.description());
            hint.setIsLocked(hr.isLocked() != null ? hr.isLocked() : false);
            hint.setProblem(problem);

            hintRepository.save(hint);  // ⚠ save first
            problem.getHints().add(hint);
        }

    }


    private void updateTestCases(Problem problem, List<TestCaseRequest> testCaseRequests) {
        log.info("Updating {} test cases", testCaseRequests.size());

        // Step 1: Remove all old test cases from collection
        problem.getTestCases().clear();

        // Step 2: Delete old test cases from database explicitly
        testCaseRepository.deleteByProblemId(problem.getId());

        // Step 3: Flush to execute the deletes immediately
        testCaseRepository.flush();

        // Step 4: Create and save new test cases directly
        List<TestCase> newTestCases = new ArrayList<>();
        for (TestCaseRequest tcr : testCaseRequests) {
            TestCase testCase = new TestCase();
            testCase.setInput(tcr.input());
            testCase.setExpectedOutput(tcr.expectedOutput());
            testCase.setProblem(problem);
            newTestCases.add(testCase);
        }

        // Step 5: Save all new test cases in batch
        if (!newTestCases.isEmpty()) {
            List<TestCase> savedTestCases = testCaseRepository.saveAll(newTestCases);
            // Step 6: Add saved test cases back to problem
            problem.getTestCases().addAll(savedTestCases);
        }
    }

    private void updateTags(Problem problem, List<String> tagNames) {
        Set<Tag> tags = tagNames.stream()
                .map(tagName -> tagRepository.findByTagName(tagName)
                        .orElseGet(() -> tagRepository.save(Tag.builder().tagName(tagName).build())))
                .collect(Collectors.toSet());
        problem.setTags(tags);
    }

    private void updateProblemIndex(Problem problem) {
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
                .tags(problem.getTags().stream()
                        .map(Tag::getTagName)
                        .collect(Collectors.toSet()))
                .build();
        problemElasticsearchRepository.save(problemIndex);
    }

    @Override
    public List<ProblemResponse> getProblemsByAuthor(String username) {
        return problemRepository.findProblemsByAuthor_UsernameAndIsDeletedFalse(username)
                .stream()
                .map(problemMapper::fromEntityToResponse)
                .toList();
    }

    @Override
    public void deleteProblemById(long problemId, String username) {
        Problem problem = problemRepository.findProblemByIdAndAuthor_UsernameAndIsDeletedFalse(problemId, username).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND ,"Problem not found")
        );
        problem.setIsDeleted(true);
        problem.setIsVerified(false);
        problem.setTitle(UUID.randomUUID().toString());
        problem.setStatus(Status.REJECTED);
        problemRepository.save(problem);
    }

    @Override
    public void rejectProblemById(long problemId) {
        Problem problem =  problemRepository.findById(problemId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND ,"Problem not found")
        );

        if (problem.getStatus().equals(Status.PENDING)) {
            problem.setIsVerified(false);
            problem.setStatus(Status.REJECTED);
            problemRepository.save(problem);
        }
        else if (problem.getStatus().equals(Status.REJECTED))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Problem already rejected");
        else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Problem not found");

    }

    @Override
    public List<String> getAllProblemTags() {
        return tagRepository.findAll().stream().map(Tag::getTagName).toList();
    }

    @Override
    public UserProblemResponse userProblems(String username) {

        // 1. Find current user
        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // 2. Get all solved problems for this user
        List<UserProblem> userProblems = userProblemRepository.findAllByUserIdAndIsSolvedTrue(user.getId());

        // 3. Count total problems
        long totalProblems = problemRepository.count();

        // Avoid division by zero
        if (totalProblems == 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No problems available in the system");

        // 4. Count how many problems this user solved
        long userProblemCount = userProblems.size();

        // 5. Calculate solve percentage (rounded to 2 decimal places)
        double percentage = (double) userProblemCount / totalProblems * 100.0;
        percentage = Math.round(percentage * 100.0) / 100.0;

        // 6. Map user problems to response objects (cleaner stream usage)
        List<ProblemAndSolvedResponse> problemAndSolvedResponses = userProblems.stream()
                .map(p -> ProblemAndSolvedResponse.builder()
                        .isSolved(p.getIsSolved())
                        .problemId(p.getProblem().getId())
                        .build())
                .toList();

        // 7. Return response
        return new UserProblemResponse(problemAndSolvedResponses, userProblemCount, totalProblems, percentage);
    }


}
