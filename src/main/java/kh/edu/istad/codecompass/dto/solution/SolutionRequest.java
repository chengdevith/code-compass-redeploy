package kh.edu.istad.codecompass.dto.solution;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

@Builder
public record SolutionRequest(
        String explanation,
        @JsonProperty("source_code")
        @NotBlank(message = "Source code is required")
        String sourceCode,

        @NotNull(message = "Problem ID is required")
        @Positive(message = "Problem ID should be positive")
        @JsonProperty("problem_id")
        Long problemId
) {
}
