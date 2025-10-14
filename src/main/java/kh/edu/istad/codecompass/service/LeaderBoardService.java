package kh.edu.istad.codecompass.service;

import kh.edu.istad.codecompass.dto.leaderboard.LeaderboardPublicResponse;
import kh.edu.istad.codecompass.dto.leaderboard.LeaderboardResponse;

public interface LeaderBoardService {

    /**
     * Retrieves the global leaderboard along with a specific user's rank and nearby competitors.
     * <p>
     * This method provides a comprehensive view of the leaderboard, showing the top-ranked
     * users, as well as the position of the specified user and their immediate neighbors.
     *
     * @param username The unique username of the user for whom to retrieve personalized leaderboard data.
     * @return A {@link LeaderboardResponse} object containing the following: the user's specific ranking details,
     * a list of the top 25 users on the leaderboard, and a list of users ranked immediately above and below them.
     * @author Panharoth
     */
    LeaderboardResponse getLeaderboardWithUser(String username);

    LeaderboardPublicResponse getLeaderboardPublic();

}
