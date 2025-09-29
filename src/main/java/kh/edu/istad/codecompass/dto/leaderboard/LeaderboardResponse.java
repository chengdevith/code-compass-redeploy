package kh.edu.istad.codecompass.dto.leaderboard;

import com.fasterxml.jackson.annotation.JsonProperty;
import kh.edu.istad.codecompass.domain.User;
import kh.edu.istad.codecompass.dto.userLeaderBoard.UserResponseLeaderBoard;
import kh.edu.istad.codecompass.mapper.BadgeMapper;

import java.util.List;

public record LeaderboardResponse(
        @JsonProperty("top_subscribers")
        List<UserResponseLeaderBoard> topUsers,
        @JsonProperty("your_rank")
        Long yourRank,
        @JsonProperty("nearby_subscribers")
        List<UserResponseLeaderBoard> nearbyUsers,
        @JsonProperty("current_subscribers")
        UserResponseLeaderBoard currentUser
) {
    public static LeaderboardResponse fromEntities(
            List<User> topUsers,
            Long yourRank,
            List<User> nearbyUsers,
            User currentUser,
            BadgeMapper badgeMapper
    ) {
        return new LeaderboardResponse(
                topUsers.stream().map(u -> UserResponseLeaderBoard.fromEntity(u, badgeMapper)).toList(),
                yourRank,
                nearbyUsers.stream().map(u -> UserResponseLeaderBoard.fromEntity(u, badgeMapper)).toList(),
                UserResponseLeaderBoard.fromEntity(currentUser, badgeMapper)
        );
    }
}

