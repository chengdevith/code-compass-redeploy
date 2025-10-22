package kh.edu.istad.codecompass.elasticsearch.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import kh.edu.istad.codecompass.enums.Difficulty;

import java.util.Set;

public record SearchProblemResponse(
        long id,
        String title,
        @JsonProperty("tags")
        Set<String> tags,
        Difficulty difficulty
) {
}
