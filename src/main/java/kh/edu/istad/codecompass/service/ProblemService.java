package kh.edu.istad.codecompass.service;

import kh.edu.istad.codecompass.dto.problem.CreateProblemRequest;
import kh.edu.istad.codecompass.dto.problem.ProblemResponse;

public interface ProblemService {

    /**
     * Creates a new coding problem with the provided details.
     * <p>
     *
     * @param problemRequest A {@link CreateProblemRequest} object containing all the details
     * of the problem.
     * @param author The unique name of the user creating the problem.
     * @return A {@link ProblemResponse} object containing the details of the newly created
     * problem, including its unique ID.
     */
    ProblemResponse createProblem(CreateProblemRequest problemRequest, String author);


    /**
     * Retrieves a specific coding problem by its unique ID.
     *
     * @param problemId The unique identifier of the problem to retrieve.
     * @return A {@link ProblemResponse} object containing the details of the requested problem.
     */
    ProblemResponse getProblem(long problemId);

    /**
     *
     * @param problemId The unique identifier of the problem to update.
     */
    void verifyProblem(long problemId, boolean isVerified);
}
