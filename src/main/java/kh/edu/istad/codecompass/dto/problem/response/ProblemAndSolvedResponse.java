package kh.edu.istad.codecompass.dto.problem.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record ProblemAndSolvedResponse(
        @JsonProperty("problem_id")
        long problemId,
        @JsonProperty("is_solved")
        boolean isSolved
) {
}
