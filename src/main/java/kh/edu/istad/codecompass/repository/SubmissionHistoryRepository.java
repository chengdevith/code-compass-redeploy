package kh.edu.istad.codecompass.repository;

import jakarta.transaction.Transactional;
import kh.edu.istad.codecompass.domain.SubmissionHistories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SubmissionHistoryRepository extends JpaRepository<SubmissionHistories, Long> {

    Integer countByUser_Username(String username);

    List<SubmissionHistories> findByUser_UsernameOrderBySubmittedAtAsc(String username);

    @Modifying
    @Transactional
    @Query("DELETE FROM SubmissionHistories s WHERE s.id = :id")
    void deleteByIdCustom(@Param("id") Long id);

    boolean existsByStatusAndUser_Username(String status, String userUsername);

}
