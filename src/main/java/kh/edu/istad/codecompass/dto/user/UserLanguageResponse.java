package kh.edu.istad.codecompass.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UserLanguageResponse(
        @JsonProperty("language_id")
        String languageId,
        @JsonProperty("total_problem_solved_by_language")
        Long totalProblemSolvedByLanguage
) {
}
