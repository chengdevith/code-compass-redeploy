package kh.edu.istad.codecompass.service;

import kh.edu.istad.codecompass.domain.Problem;
import kh.edu.istad.codecompass.dto.problem.CreateProblemRequest;
import kh.edu.istad.codecompass.dto.problem.ProblemResponse;

import java.util.List;

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
     * Retrieves a list of all problems, including both verified and unverified ones.
     * This method is intended for use by administrators to manage the full set of
     * available problems.
     *
     * @return A {@link List} of {@link ProblemResponse} objects, each containing the details of a problem.
     * The list may be empty if no problems exist.
     */
    List<ProblemResponse> getProblems();

    /**
     * Retrieves a list of all unverified problems.
     * <p>
     * This method serves as a filtering feature for administrators to easily view
     * and manage problems that have not yet been approved.
     *
     * @return A {@link List} of {@link ProblemResponse} objects representing all problems
     * that are currently unverified. The list will be empty if all problems are verified
     */
    List<ProblemResponse> getUnverifiedProblems();

    /**
     * Retrieves a list of all verified problems.
     * <p>
     * This method is a key feature for both administrators—who use it to view approved content—and
     * subscribers, who can access this list as a curated set of problems.
     *
     * @return A {@link List} of {@link ProblemResponse} objects, each containing the details of a
     * verified problem. The list will be empty if there are no verified problems.
     */
    List<ProblemResponse> getVerifiedProblems();

    /**
     * Verifies or un-verifies a problem, changing its public visibility.
     * <p>
     * This method is used by administrators to approve a problem for public
     * viewing or to revoke its verified status.
     *
     * @param problemId The unique identifier of the problem to verify or un-verify.
     * @param isVerified A boolean flag; {@code true} to verify the problem, {@code false} to un-verify it.
     */
    void verifyProblem(long problemId, boolean isVerified);
}
