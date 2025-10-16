package kh.edu.istad.codecompass.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import kh.edu.istad.codecompass.enums.Role;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
public record UserProfileResponse(
        String username,
        String email,
        String gender,
        String dob,
        String bio,
        String location,
        String website,
        String github,
        String linkedin,
        @JsonProperty("image_url")
        String imageUrl,
        String level,
        Integer coin,
        Integer star,
        Long rank,
        @JsonProperty("total_problem_solved")
        Integer totalProblemsSolved,
        @JsonProperty("is_deleted")
        Boolean isDeleted,
        Integer badge,
        @JsonProperty("submission_histories")
        Integer submissionHistories,
        Integer solution,
        Integer view,
        Integer comment,
        Role role,
        @JsonProperty("user_language_responses")
        List<UserLanguageResponse> userLanguageResponse
) {
}
