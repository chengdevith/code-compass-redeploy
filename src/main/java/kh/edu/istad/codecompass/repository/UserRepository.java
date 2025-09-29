package kh.edu.istad.codecompass.repository;

import kh.edu.istad.codecompass.domain.LeaderBoard;
import kh.edu.istad.codecompass.domain.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {

    Optional<User> findUserByUsername(String username);

    Boolean existsByUsername(String username);

    List<User> findAllByOrderByStarDesc();

    List<User> findByLeaderBoardOrderByRankAsc(LeaderBoard leaderBoard, Pageable pageable);

    List<User> findByLeaderBoardAndRankBetweenOrderByRankAsc(
            LeaderBoard leaderBoard,
            Long startRank,
            Long endRank
    );

    // Count total users in leaderboard for edge case handling
    Long countByLeaderBoard(LeaderBoard leaderBoard);

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    List<User> findAll();
}
