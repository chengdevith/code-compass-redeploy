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
public record UpdateProblemRequest(
        @Size(max = 100, message = "Title cannot be more than 100 characters")
        String title,

        @Size(max = 5000, message = "Description cannot be more than 5000 characters")
        String description,

        Difficulty difficulty,

        Star star,

        @Size(min = 20, max = 40, message = "The coins should be between 20 to 40")
        Integer coin,

        @JsonProperty("best_memory_usage")
        @Min(value = 0, message = "Best memory usage should be positive")
        Integer bestMemoryUsage,

        @JsonProperty("best_time_execution")
        @Min(value = 0, message = "Best time execution")
        Double bestTimeExecution,

        @JsonProperty("test_cases")
        List<TestCaseRequest> testCases,

        @JsonProperty("tag_names")
        List<String> tagNames,

        List<HintRequest> hints
) {
}
