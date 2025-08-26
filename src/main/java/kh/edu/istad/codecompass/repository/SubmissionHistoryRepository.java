package kh.edu.istad.codecompass.repository;

import kh.edu.istad.codecompass.domain.SubmissionHistories;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubmissionHistoryRepository extends JpaRepository<SubmissionHistories, Long> {

    Integer countSubmissionHistoriesByUser_Username(String userUsername);

    List<SubmissionHistories> findSubmissionHistoriesByUser_Username(String userUsername);

    boolean existsByStatusAndUser_Username(String status, String userUsername);

}
