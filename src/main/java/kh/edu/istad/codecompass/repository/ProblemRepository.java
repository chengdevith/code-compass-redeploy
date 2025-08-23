package kh.edu.istad.codecompass.repository;

import kh.edu.istad.codecompass.domain.Problem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProblemRepository extends JpaRepository<Problem, Long> {
    boolean existsProblemByTitle(String title);
}
