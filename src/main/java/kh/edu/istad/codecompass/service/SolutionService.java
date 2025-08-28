package kh.edu.istad.codecompass.service;

import kh.edu.istad.codecompass.dto.solution.SolutionRequest;
import kh.edu.istad.codecompass.dto.solution.SolutionResponse;

import java.util.List;

public interface SolutionService {

    SolutionResponse postSolution(SolutionRequest request, String author);

    List<SolutionResponse> getAllSolutions(String username, Long problemId);

}
