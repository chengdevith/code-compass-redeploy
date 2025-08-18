package kh.edu.istad.codecompass.service;

import kh.edu.istad.codecompass.dto.jugde0.*;

public interface Judge0Service {
    //    single submission
    SubmissionResult createSubmission(CreateSubmissionRequest request);
    SubmissionResult getSubmissionByToken(String token);

    //    batch submission
    Judge0BatchResponse createSubmissionBatch(BatchSubmissionRequest batchRequest);
//    Judge0BatchResponse getBatchSubmissions(String tokens, boolean base64Encoded, String fields);
}
