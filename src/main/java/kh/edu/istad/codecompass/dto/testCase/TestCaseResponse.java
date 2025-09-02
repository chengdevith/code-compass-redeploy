package kh.edu.istad.codecompass.dto.testCase;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TestCaseResponse(
        @JsonProperty("stdin")
        String input,
        @JsonProperty("expected_outputs")
        String expectedOutput
) {}

