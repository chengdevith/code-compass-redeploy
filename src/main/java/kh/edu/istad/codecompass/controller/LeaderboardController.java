package kh.edu.istad.codecompass.controller;

import kh.edu.istad.codecompass.dto.leaderboard.LeaderboardResponse;
import kh.edu.istad.codecompass.service.LeaderBoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/code-compass/leaderboard")
@RequiredArgsConstructor
public class LeaderboardController {

    private final LeaderBoardService leaderBoardService;

    @GetMapping("/me")
    public LeaderboardResponse getLeaderboard(@AuthenticationPrincipal Jwt jwt) {
        String username = jwt.getClaim("preferred_username");
        return leaderBoardService.getLeaderboardWithUser(username);
    }

}
