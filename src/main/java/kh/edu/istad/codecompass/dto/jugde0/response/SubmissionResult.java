package kh.edu.istad.codecompass.dto.jugde0.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record SubmissionResult(
        String token,
        @JsonProperty("std_out") String stdout,
        @JsonProperty("std_err") String stderr,
        @JsonProperty("compile_output") String compileOutput,
        @JsonProperty("language_id") String languageId,
        Status status,
        String time,
        String memory,
        String message
) {
    public record Status(Integer id, String description) {}
}
