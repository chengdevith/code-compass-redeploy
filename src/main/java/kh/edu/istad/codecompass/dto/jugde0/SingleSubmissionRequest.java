package kh.edu.istad.codecompass.dto.jugde0;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SingleSubmissionRequest(
        @JsonProperty("source_code") String sourceCode,
        @JsonProperty("language_id") String languageId,
        @JsonProperty("stdin") String stdin,
        @JsonProperty("expected_output") String expectedOutput
) {}
