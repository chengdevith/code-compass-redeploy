package kh.edu.istad.codecompass.dto.problem.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import kh.edu.istad.codecompass.dto.hint.HintRequest;
import kh.edu.istad.codecompass.dto.testCase.TestCaseRequest;
import kh.edu.istad.codecompass.enums.Difficulty;
import kh.edu.istad.codecompass.enums.Star;
import lombok.Builder;

import java.util.List;

@Builder
public record CreateProblemRequest (
        @NotBlank(message = "Title is required")
        @Size(max = 100, message = "Title cannot be more than 100 characters")
        String title,

        @NotBlank(message = "Description is required")
        @Size(max = 5000, message = "Description cannot be more than 5000 characters")
        String description,

        @NotNull(message = "Difficulty is required")
        Difficulty difficulty,

        @NotNull(message = "Star is required")
        Star star,

        @NotNull
        @Max(40)
        @Min(20)
        Integer coin,

        @JsonProperty("best_memory_usage")
        @Min(value = 0, message = "Best memory usage should be positive")
        Integer bestMemoryUsage,

        @JsonProperty("best_time_execution")
        @Min(value = 0, message = "Best time execution")
        Double bestTimeExecution,

        @JsonProperty("test_cases")
        @NotEmpty(message = "At least one test case is required")
        List<TestCaseRequest> testCases,

        @JsonProperty("tag_names")
        @NotEmpty(message = "At least one tag is required")
        List<String> tagNames,

        List<HintRequest> hints
) { }
