package kh.edu.istad.codecompass.dto;

import jakarta.validation.constraints.NotBlank;

public record TestCaseRequest(
        @NotBlank(message = "Input is required") String input,
        @NotBlank(message = "Expected output is required") String expectedOutput
) {}
