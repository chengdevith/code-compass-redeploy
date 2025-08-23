package kh.edu.istad.codecompass.dto.jugde0.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record BatchSubmissionRequest(
        @JsonProperty("source_code") String sourceCode,
        @JsonProperty("language_id") String languageId,
        @JsonProperty("stdin") List<String> inputs,
        @JsonProperty("expected_outputs") List<String> expectedOutputs
) {}

