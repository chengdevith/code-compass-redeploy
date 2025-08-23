package kh.edu.istad.codecompass.dto.jugde0.response;

import java.util.List;

public record Judge0BatchResponse(
        List<Judge0SubmissionResponse> submissions
) { }