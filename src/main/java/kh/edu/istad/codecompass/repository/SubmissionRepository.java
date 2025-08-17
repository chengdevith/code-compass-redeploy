package kh.edu.istad.codecompass.repository;

import kh.edu.istad.codecompass.domain.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    Optional<Submission> findByToken(String token);
}