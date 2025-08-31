package kh.edu.istad.codecompass.dto.packageDTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record AddProblemToPackageRequest(
        @NotNull(message = "Package id is required")
        @JsonProperty("package_name")
        String packageName,

        @NotEmpty(message = "Problem ID is required at least one")
        @JsonProperty("problem_ids")
        List<Long> problemIds
) {
}
