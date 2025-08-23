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

    //    batch submission

    /**
     * Submits a batch of code submission requests to the Judge0 service.
     * This method is designed to handle multiple submissions simultaneously,
     * returning a single response that contains the results for each submission.
     *
     * @param batchRequest A {@link BatchSubmissionRequest} object containing a list of individual submission requests.
     * @return A {@link Judge0BatchResponse} containing the results for all submissions in the batch.
     * @author Panharoth
     */
    Judge0BatchResponse createSubmissionBatch(BatchSubmissionRequest batchRequest);
}
