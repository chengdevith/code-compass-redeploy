package kh.edu.istad.codecompass.dto.problem.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import kh.edu.istad.codecompass.dto.testCase.TestCaseResponse;
import kh.edu.istad.codecompass.dto.hint.response.UserHintResponse;
import kh.edu.istad.codecompass.enums.Difficulty;
import kh.edu.istad.codecompass.enums.Star;

import java.util.List;

public record ProblemResponseBySpecificUser (
        Long id,

        @JsonProperty("best_memory_usage")
        Integer bestMemoryUsage,

        @JsonProperty("best_time_execution")
        Double bestTimeExecution,

        Integer coin,

        String description,

        Difficulty difficulty,

        Star star,

        String title,

        @JsonProperty("test_cases")
        List<TestCaseResponse> testCases,

        @JsonProperty("tag_names")
        List<String> tags,

        List<UserHintResponse> hints,

        String author,

        @JsonProperty("is_deleted")
        Boolean isDeleted,

        @JsonProperty("is_verified")
        Boolean isVerified
){
}
