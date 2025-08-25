package kh.edu.istad.codecompass.service;

import kh.edu.istad.codecompass.dto.problem.CreateProblemRequest;
import kh.edu.istad.codecompass.dto.problem.ProblemResponse;

public interface ProblemService {

    /**
     *
     * @param problemRequest
     * @param author
     * @return {@link ProblemResponse}
     */
    ProblemResponse createProblem(CreateProblemRequest problemRequest, String author);


    ProblemResponse getProblem(long problemId);
}
