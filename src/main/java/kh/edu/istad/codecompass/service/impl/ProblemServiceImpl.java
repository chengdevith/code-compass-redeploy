package kh.edu.istad.codecompass.service.impl;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import kh.edu.istad.codecompass.domain.Problem;
import kh.edu.istad.codecompass.domain.Tag;
import kh.edu.istad.codecompass.domain.TestCase;
import kh.edu.istad.codecompass.dto.TestCaseRequest;
import kh.edu.istad.codecompass.dto.problem.CreateProblemRequest;
import kh.edu.istad.codecompass.dto.problem.ProblemResponse;
import kh.edu.istad.codecompass.mapper.ProblemMapper;
import kh.edu.istad.codecompass.repository.ProblemRepository;
import kh.edu.istad.codecompass.repository.TagRepository;
import kh.edu.istad.codecompass.service.ProblemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProblemServiceImpl implements ProblemService {

    private final ProblemRepository problemRepository;
    private final ProblemMapper problemMapper;
    private final TagRepository tagRepository;

    @Override
    public ProblemResponse createProblem(CreateProblemRequest problemRequest) {

        if (problemRepository.existsProblemByTitle(problemRequest.title()))
            throw new BadRequestException("Problem already exists");

        Problem problem = problemMapper.fromRequestToEntity(problemRequest);

        // Map test cases
        Problem finalProblem = problem;
        List<TestCase> testCases = (problemRequest.testCases() == null ? List.<TestCaseRequest>of() : problemRequest.testCases())
                .stream()
                .map(tc -> problemMapper.toTestCase(tc, finalProblem))
                .toList();

        log.info("Test cases {}", testCases.toString());

        problem.setTestCases(testCases);


        // Map tags
        Set<Tag> tags = problemRequest.tagNames().stream()
                .map(name -> tagRepository.findByTagName(name)
                        .orElseGet(() -> tagRepository.save(new Tag(name)))) // save if not exist
                .collect(Collectors.toSet());

        problem.setTags(tags);

        problem.setBestMemoryUsage(problemRequest.bestMemoryUsage());
        problem.setBestTimeExecution(problemRequest.bestTimeExecution());
        problem.setIsLocked(false);
        problem.setIsDeleted(false);

        problem = problemRepository.save(problem);

        return problemMapper.fromEntityToResponse(problem);
    }

    @Transactional
    @Override
    public ProblemResponse getProblem(long problemId) {

        Problem problem = problemRepository.findProblemById(problemId).orElseThrow(
                () -> new NotFoundException("Problem not found")
        );

        return problemMapper.fromEntityToResponse(problem);
    }
}
