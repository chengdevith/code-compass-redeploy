package kh.edu.istad.codecompass.service;

import kh.edu.istad.codecompass.dto.problem.CreateProblemRequest;
import kh.edu.istad.codecompass.dto.problem.ProblemResponse;

public interface ProblemService {
    ProblemResponse createProblem(CreateProblemRequest problemRequest);
    ProblemResponse getProblem(long problemId);
}
