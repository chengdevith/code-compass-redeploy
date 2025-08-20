package kh.edu.istad.codecompass.dto.jugde0;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Judge0SingleResponse(
        @JsonProperty("language_id") String languageId,
        String stdout,
        String time,
        Integer memory,
        String stderr,
        String token,
        @JsonProperty("compile_output") String compileOutput,
        String message,
        Judge0SubmissionResponse.Status status
) {}
