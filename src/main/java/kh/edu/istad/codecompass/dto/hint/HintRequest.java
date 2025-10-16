package kh.edu.istad.codecompass.dto.hint;

import com.fasterxml.jackson.annotation.JsonProperty;

public record HintRequest (
        Long id,
        String description,
        @JsonProperty("is_locked")
        Boolean isLocked
) {
}
