package kh.edu.istad.codecompass.repository;

import kh.edu.istad.codecompass.domain.Problem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProblemRepository extends JpaRepository<Problem, Long> {

    boolean existsProblemByTitleAndIsDeletedFalse(String title);

    Optional<Problem> findProblemByIdAndIsVerifiedFalseAndIsDeletedFalse(long problemId);

    Optional<Problem> findProblemByIdAndIsVerifiedTrue(long problemId);

    Page<Problem> findProblemsByIsVerifiedFalseAndIsDeletedFalse(Pageable pageable);

    Page<Problem> findProblemsByIsVerifiedTrue(Pageable pageable);

    Optional<Problem> findProblemByIdAndAuthor_UsernameAndIsDeletedFalse(Long problemId, String authorUsername);

    List<Problem> findProblemsByAuthor_UsernameAndIsDeletedFalse(String username);

    List<Problem> findByIsDeletedFalse();

}
