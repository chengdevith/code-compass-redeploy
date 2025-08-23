package kh.edu.istad.codecompass.dto.jugde0.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record Judge0BatchRequest(
        @JsonProperty("submissions") List<CreateSubmissionRequest> submissions,
        @JsonProperty("base64_encoded") Boolean base64Encoded
) {
    public Judge0BatchRequest (List<CreateSubmissionRequest> submissions) {
        this(submissions, false);
    }
}