package kh.edu.istad.codecompass.dto.leaderboard;

import kh.edu.istad.codecompass.dto.user.UserProfileResponse;

import java.util.List;

public record LeaderboardPublicResponse(
        List<UserProfileResponse> userProfileResponses
) {
}
