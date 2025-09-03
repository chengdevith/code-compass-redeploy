package kh.edu.istad.codecompass.repository;

import kh.edu.istad.codecompass.domain.LeaderBoard;
import kh.edu.istad.codecompass.domain.User;
import kh.edu.istad.codecompass.domain.UserProblem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.awt.print.Pageable;
import java.util.List;
import java.util.Optional;

public interface UserProblemRepository extends JpaRepository<UserProblem, Long> {

    List<UserProblem> findAllByUserIdAndIsSolvedTrue(Long userId);

}
