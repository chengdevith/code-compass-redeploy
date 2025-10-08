package kh.edu.istad.codecompass.service;

import kh.edu.istad.codecompass.dto.solution.SolutionRequest;
import kh.edu.istad.codecompass.dto.solution.SolutionResponse;

import java.util.List;

public interface SolutionService {

    /**
     * This method is for creators or subscribers to post their solutions after solving a problem.
     * It validates the submission and saves it to the database, linking it to the problem and author.
     *
     * @param request A {@link SolutionRequest} object containing the solution details, such as the code, language, and any explanations.
     * @param author The username of the person posting the solution.
     * @return A {@link SolutionResponse} object representing the solution that was just posted, including its unique ID and other details.
     * @author Panharoth
     */
    SolutionResponse postSolution(SolutionRequest request, String author);

    /**
     * Retrieves all solutions for a specific problem that are accessible to a user.
     * This method is exclusively for subscribers who have successfully solved the problem,
     * ensuring that solutions are only visible to those who have completed the challenge themselves.
     *
     * @param username  The username of the subscriber who is accessing the solutions. This is used to verify their eligibility.
     * @param problemId The unique ID of the problem for which to retrieve solutions.
     * @return A {@link List} of {@link SolutionResponse} objects, each representing a solution to the specified problem.
     * The list will be empty if no solutions are available or if the user is not eligible to view them.
     * @author Panharoth
     */
    List<SolutionResponse> getAllSolutions(String username, Long problemId);

    void deleteSolution(Long solutionId, String author);

}
