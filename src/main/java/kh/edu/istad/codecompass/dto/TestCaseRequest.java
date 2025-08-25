package kh.edu.istad.codecompass.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public record TestCaseRequest(
        @NotBlank(message = "Input is required")
        @JsonProperty("stdin")
        String input,

        @NotBlank(message = "Expected output is required")
        @JsonProperty("expected_outputs")
        String expectedOutput
) {}
