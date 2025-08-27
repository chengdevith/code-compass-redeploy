package kh.edu.istad.codecompass.service;

import kh.edu.istad.codecompass.dto.solution.SolutionRequest;
import kh.edu.istad.codecompass.dto.solution.SolutionResponse;

public interface SolutionService {

    SolutionResponse postSolution(SolutionRequest request, String author);

}
