package kh.edu.istad.codecompass.dto.solution;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import org.hibernate.validator.constraints.Length;

@Builder
public record SolutionRequest(
        String explanation,
        @JsonProperty("source_code")
        @NotBlank(message = "Source code is required")
        String sourceCode,

        @NotNull(message = "Problem ID is required")
        @Positive(message = "Problem ID should be positive")
        @JsonProperty("problem_id")
        Long problemId,

        @JsonProperty("language_id")
        @NotBlank(message = "Programming language ID is required")
        @Length(max = 99, message = "Programming language ID should be between 1 to 99")
        String languageId
) {

}
