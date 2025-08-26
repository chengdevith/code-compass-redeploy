package kh.edu.istad.codecompass.service;

import kh.edu.istad.codecompass.dto.jugde0.request.BatchSubmissionRequest;
import kh.edu.istad.codecompass.dto.jugde0.request.CreateSubmissionRequest;
import kh.edu.istad.codecompass.dto.jugde0.response.Judge0BatchResponse;
import kh.edu.istad.codecompass.dto.jugde0.response.Judge0SubmissionResponse;
import kh.edu.istad.codecompass.dto.jugde0.response.SubmissionResult;

public interface Judge0Service {

    /**
     * Submits a coding request to the Judge0 service and retrieves the result instantly.
     * @param request A {@link CreateSubmissionRequest} object containing the source code and language details.
     * @return  A {@link SubmissionResult} object with the execution status, output, and performance metrics (e.g., time, memory).
     * @author Panharoth
     */
    SubmissionResult createSubmission(CreateSubmissionRequest request);

    /**
     * Get a submission result from submission token
     * @param token A submission token
     * @return A {@link SubmissionResult} object with the execution status, output, and performance metrics (e.g., time, memory).
     * @author Panharoth
     */
    Judge0SubmissionResponse getSubmissionByToken(String token);

    //===batch submission===

    /**
     * Submits a batch of code submission requests to the Judge0 service.
     * <p>
     * This method is designed to handle multiple submissions simultaneously,
     * returning a single response that contains the results for each submission.
     * It also records the submissions to the user's history for a specific problem.
     *
     * @param batchRequest A {@link BatchSubmissionRequest} object containing a list
     * of individual submission requests (e.g., source code, language ID, etc.).
     * @param username The unique identifier of the user making the submissions.
     * This is used to link the submissions to a user's account for tracking history.
     * @param problemId The unique ID of the problem to which these submissions belong.
     * @return A {@link Judge0BatchResponse} containing the results for all
     * submissions in the batch.
     * @author Panharoth
     */
    Judge0BatchResponse createSubmissionBatch(BatchSubmissionRequest batchRequest, String username, Long problemId);

    /**
     * This method is specific to run the code without saving to user submission history.
     * It is used for a "run and test" feature, where a user wants to check
     * their code against test cases without making a formal submission.
     *
     * @param batchRequest A {@link BatchSubmissionRequest} object containing one or more submission requests to be executed.
     * @return A {@link Judge0BatchResponse} containing the execution results for the provided batch of submissions.
     * @author Panharoth
     */
    Judge0BatchResponse runSubmissionBatch(BatchSubmissionRequest batchRequest);
}
