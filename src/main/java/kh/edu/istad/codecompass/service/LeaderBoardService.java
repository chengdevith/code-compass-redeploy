package kh.edu.istad.codecompass.service;

import kh.edu.istad.codecompass.dto.leaderboard.LeaderboardResponse;

public interface LeaderBoardService {

    LeaderboardResponse getLeaderboardWithUser(String username);

}
