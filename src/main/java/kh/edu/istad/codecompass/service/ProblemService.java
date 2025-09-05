package kh.edu.istad.codecompass.service;

import kh.edu.istad.codecompass.dto.problem.request.CreateProblemRequest;
import kh.edu.istad.codecompass.dto.problem.response.ProblemResponse;
import kh.edu.istad.codecompass.dto.problem.response.ProblemResponseBySpecificUser;
import kh.edu.istad.codecompass.dto.problem.request.UpdateProblemRequest;
import kh.edu.istad.codecompass.dto.problem.response.ProblemSummaryResponse;

import java.util.List;

public interface ProblemService {

    /**
     * Creates a new coding problem with the provided details.
     * @param problemRequest A {@link CreateProblemRequest} object containing all the details
     * of the problem.
     * @param author The unique name of the user creating the problem.
     * @return A {@link ProblemResponse} object containing the details of the newly created
     * problem, including its unique ID.
     * @author Panharoth
     */
    ProblemResponse createProblem(CreateProblemRequest problemRequest, String author);


    /**
     * Retrieves a problem tailored to a specific user, including their submission history and hints they've unlocked on that problem.
     * This method is for providing a personalized view of a problem to a user.
     *
     * @param username  The username of the user for whom the problem data is being retrieved.
     * @param problemId The unique identifier of the problem.
     * @return A {@link ProblemResponseBySpecificUser} object that contains problem details along with the user's past submissions and status for that problem.
     * @author Panharoth
     */
    ProblemResponseBySpecificUser getProblemBySpecificUser(String username, long problemId);

    /**
     * Retrieves a specific coding problem by its unique ID.
     *
     * @param problemId The unique identifier of the problem to retrieve.
     * @return A {@link ProblemResponse} object containing the details of the requested problem.
     * @author Panharoth
     */
    ProblemResponse getProblem(long problemId);

    /**
     * Retrieves a list of all problems, including both verified and unverified ones.
     * This method is intended for use by administrators to manage the full set of
     * available problems.
     *
     * @return A {@link List} of {@link ProblemSummaryResponse} objects, each containing the details of a problem.
     * The list may be empty if no problems exist.
     * @author Panharoth
     */
    List<ProblemSummaryResponse> getProblems();

    /**
     * Retrieves a list of all unverified problems.
     * <p>
     * This method serves as a filtering feature for administrators to easily view
     * and manage problems that have not yet been approved.
     *
     * @return A {@link List} of {@link ProblemSummaryResponse} objects representing all problems
     * that are currently unverified. The list will be empty if all problems are verified
     * @author Panharoth
     */
    List<ProblemSummaryResponse> getUnverifiedProblems();

    /**
     * Retrieves a list of all verified problems.
     * <p>
     * This method is a key feature for both administrators—who use it to view approved content—and
     * subscribers, who can access this list as a curated set of problems.
     *
     * @return A {@link List} of {@link ProblemSummaryResponse} objects, each containing the details of a
     * verified problem. The list will be empty if there are no verified problems.
     * @author Panharoth
     */
    List<ProblemSummaryResponse> getVerifiedProblems();

    /**
     * Verifies or un-verifies a problem, changing its public visibility.
     * <p>
     * This method is used by administrators to approve a problem for public
     * viewing or to revoke its verified status.
     *
     * @param problemId The unique identifier of the problem to verify or un-verify.
     * @param isVerified A boolean flag; {@code true} to verify the problem, {@code false} to un-verify it.
     * @author Panharoth
     */
    void verifyProblem(long problemId, boolean isVerified);


    /**
     * Updates an existing problem created by a specific user.
     * <p>
     * This method allows a problem's author to edit its details. It ensures that
     * only the original creator can modify the problem.
     *
     * @param problemId The unique identifier of the problem to be updated.
     * @param username  The username of the user who is attempting to update the problem.
     * This is used to verify that the user is the problem's original author.
     * @author Panharoth
     */
    void updateProblem(Long problemId, String username, UpdateProblemRequest updateProblemRequest);
}
