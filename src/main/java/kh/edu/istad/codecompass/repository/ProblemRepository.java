package kh.edu.istad.codecompass.repository;

import kh.edu.istad.codecompass.domain.Problem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProblemRepository extends JpaRepository<Problem, Long> {
    boolean existsProblemByTitle(String title);

    Optional<Problem> findProblemByIdAndIsVerifiedTrue(Long id);
}
