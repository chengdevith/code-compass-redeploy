package kh.edu.istad.codecompass.dto.jugde0;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record Judge0SubmissionResponse(
        @JsonProperty("language_id") String languageId,
        String stdout,
        String time,
        Integer memory,
        String stderr,
        String token,
        @JsonProperty("compile_output") String compileOutput,
        String message,
        Status status
) {
    public record Status(Integer id, String description) {}
}