package kh.edu.istad.codecompass.dto.userLeaderBoard;

import com.fasterxml.jackson.annotation.JsonProperty;
import kh.edu.istad.codecompass.domain.User;
import kh.edu.istad.codecompass.dto.badge.response.BadgeSummaryResponse;
import kh.edu.istad.codecompass.enums.Level;
import kh.edu.istad.codecompass.mapper.BadgeMapper;

import java.util.List;

public record UserResponseLeaderBoard(
        String username,
        Long rank,
        @JsonProperty("stars") Integer star,
        @JsonProperty("total_problem_solved") Integer totalProblemsSolved,
        Level level,
        @JsonProperty("profile_image") String imageUrl,
        String location,
        List<BadgeSummaryResponse> badgesResponse
        
) {
    public static UserResponseLeaderBoard fromEntity(User user, BadgeMapper badgeMapper) {
        return new UserResponseLeaderBoard(
                user.getUsername(),
                user.getRank(),
                user.getStar(),
                user.getTotalProblemsSolved(),
                user.getLevel(),
                user.getImageUrl(),
                user.getLocation(),
                user.getBadges().stream().map(badgeMapper::toBadgeSummaryResponse).toList()
        );
    }
}

