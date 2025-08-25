package kh.edu.istad.codecompass.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record HintRequest (
        String hint,
        @JsonProperty("is_locked")
        Boolean isLocked
) {
}
