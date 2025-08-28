package kh.edu.istad.codecompass.dto.solution;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record SolutionResponse(
        @JsonProperty("source_code")
        String sourceCode,
        String explanation,
        String author,
        @JsonProperty("problem_id") Long problemId
) {
}
