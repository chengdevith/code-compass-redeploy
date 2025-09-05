package kh.edu.istad.codecompass.dto.userLeaderBoard;

import com.fasterxml.jackson.annotation.JsonProperty;
import kh.edu.istad.codecompass.domain.User;
import kh.edu.istad.codecompass.dto.badge.BadgesResponse;
import kh.edu.istad.codecompass.enums.Level;

public record UserResponseLeaderBoard(
        String username,
        Long rank,
        @JsonProperty("stars") Integer star,
        @JsonProperty("total_problem_solved") Integer totalProblemsSolved,
        Level level,
        @JsonProperty("profile_image") String imageUrl,
        String location

) {
    public static UserResponseLeaderBoard fromEntity(User user) {
        return new UserResponseLeaderBoard(
                user.getUsername(),
                user.getRank(),
                user.getStar(),
                user.getTotalProblemsSolved(),
                user.getLevel(),
                user.getImageUrl(),
                user.getLocation()
        );
    }
}

