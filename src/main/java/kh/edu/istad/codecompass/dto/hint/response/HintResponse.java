package kh.edu.istad.codecompass.dto.hint.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record HintResponse(
        Long id,
        String description,
        @JsonProperty("is_locked")
        Boolean isLocked
) {
}
