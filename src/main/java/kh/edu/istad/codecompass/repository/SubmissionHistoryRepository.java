package kh.edu.istad.codecompass.repository;

import jakarta.transaction.Transactional;
import kh.edu.istad.codecompass.domain.SubmissionHistories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SubmissionHistoryRepository extends JpaRepository<SubmissionHistories, Long> {

    @Modifying
    @Transactional
    @Query("DELETE FROM SubmissionHistories s WHERE s.user.username = :username " +
            "AND s.id NOT IN (SELECT s2.id FROM SubmissionHistories s2 " +
            "WHERE s2.user.username = :username ORDER BY s2.submittedAt DESC LIMIT 5)")
    void deleteOldSubmissions(@Param("username") String username);

    List<SubmissionHistories> findByProblemIdAndUser_Username(Long problemId, String userUsername);

}
