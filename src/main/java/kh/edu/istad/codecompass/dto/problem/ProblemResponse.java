package kh.edu.istad.codecompass.dto.problem;

import com.fasterxml.jackson.annotation.JsonProperty;
import kh.edu.istad.codecompass.dto.HintRequest;
import kh.edu.istad.codecompass.dto.HintResponse;
import kh.edu.istad.codecompass.dto.TestCaseResponse;
import kh.edu.istad.codecompass.enums.Difficulty;
import kh.edu.istad.codecompass.enums.Star;
import lombok.Builder;

import java.util.List;

@Builder
public record ProblemResponse(
        Long id,

        @JsonProperty("best_memory_usage")
        Integer bestMemoryUsage,

        @JsonProperty("best_time_execution")
        Double bestTimeExecution,

        Byte coin,

        String description,

        Difficulty difficulty,

        Star star,

        String title,

        @JsonProperty("test_cases")
        List<TestCaseResponse> testCases,

        @JsonProperty("tag_names")
        List<String> tags,

        List<HintResponse> hints,

        String author
) { }

