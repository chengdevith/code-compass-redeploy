package kh.edu.istad.codecompass.repository;

import kh.edu.istad.codecompass.domain.Problem;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface ProblemRepository extends JpaRepository<Problem, Long> {

    boolean existsProblemByTitle(String title);

    Optional<Problem> findProblemByIdAndIsVerifiedFalse(long problemId);

    Optional<Problem> findProblemByIdAndIsVerifiedTrue(long problemId);

    List<Problem> findProblemsByIsVerifiedFalse();

    List<Problem> findProblemsByIsVerifiedTrue();

    Optional<Problem> findProblemByIdAndAuthor_Username(Long problemId, String authorUsername);
}
