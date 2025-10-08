package kh.edu.istad.codecompass.dto.solution;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record SolutionResponse(
        @JsonProperty("solution_id")
        Long solutionId,
        @JsonProperty("source_code")
        String sourceCode,
        String explanation,
        String author,
        @JsonProperty("problem_id")
        Long problemId,
        @JsonProperty("language_id")
        String languageId,
        @JsonProperty("user_profile")
        String userProfile,
        @JsonProperty("posted_at")
        LocalDateTime postedAt
) {
}
