package kh.edu.istad.codecompass.dto.problem.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.List;

@Builder
public record UserProblemResponse(
        @JsonProperty("problem_and_solved")
        List<ProblemAndSolvedResponse> problemAndSolved,
        @JsonProperty("total_problem_solved")
        long totalProblemSolved,
        @JsonProperty("total_problems")
        long totalProblems,
        double percentage
) {
}
