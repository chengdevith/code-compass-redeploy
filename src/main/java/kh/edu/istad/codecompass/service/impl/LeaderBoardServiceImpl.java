package kh.edu.istad.codecompass.service.impl;

import jakarta.transaction.Transactional;
import kh.edu.istad.codecompass.domain.LeaderBoard;
import kh.edu.istad.codecompass.domain.User;
import kh.edu.istad.codecompass.dto.leaderboard.LeaderboardResponse;
import kh.edu.istad.codecompass.dto.userLeaderBoard.UserResponseLeaderBoard;
import kh.edu.istad.codecompass.mapper.BadgeMapper;
import kh.edu.istad.codecompass.repository.LeaderBoardRepository;
import kh.edu.istad.codecompass.repository.UserRepository;
import kh.edu.istad.codecompass.service.LeaderBoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaderBoardServiceImpl implements LeaderBoardService {

    private final LeaderBoardRepository leaderBoardRepository;
    private final UserRepository userRepository;
    private final BadgeMapper  badgeMapper;

    @Override
    @Transactional
    public LeaderboardResponse getLeaderboardWithUser(String username) {
        User user = userRepository.findUserByUsername(username).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found")
        );
        if (user.getIsDeleted().equals(true))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found");

        LeaderBoard leaderBoard = user.getLeaderBoard();
        Long userRank = user.getRank();

        // Get top 25 users with the highest stars
        List<UserResponseLeaderBoard> topUsers = getTop25Users(leaderBoard);

        // Get nearby users with proper edge case handling
        List<UserResponseLeaderBoard> nearbyUsers = getNearbyUsersWithEdgeCases(leaderBoard, userRank);

        // Current user response
        UserResponseLeaderBoard currentUser = UserResponseLeaderBoard.fromEntity(user, badgeMapper);

        return new LeaderboardResponse(topUsers, userRank, nearbyUsers, currentUser);
    }

    private List<UserResponseLeaderBoard> getTop25Users(LeaderBoard leaderBoard) {
        Pageable pageable = PageRequest.of(0, 25, Sort.by("rank").ascending());
        List<User> users = userRepository.findByLeaderBoardOrderByRankAsc(leaderBoard, pageable);

        return users.stream()
                .map(u -> UserResponseLeaderBoard.fromEntity(u, badgeMapper))
                .toList();
    }

    private List<UserResponseLeaderBoard> getNearbyUsersWithEdgeCases(LeaderBoard leaderBoard, Long userRank) {
        // Always try to get 3 users centered around current user
        Long startRank = Math.max(userRank - 1, 1L);
        long endRank = userRank + 1;

        // Get the total count to handle edge cases
        Long totalUsers = userRepository.countByLeaderBoard(leaderBoard);
        endRank = Math.min(endRank, totalUsers);

        List<User> users = userRepository.findByLeaderBoardAndRankBetweenOrderByRankAsc(
                leaderBoard, startRank, endRank);

        return users.stream()
                .map(u -> {
                    return UserResponseLeaderBoard.fromEntity(u, badgeMapper);
                })
                .toList();
    }
}

