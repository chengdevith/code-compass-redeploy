package kh.edu.istad.codecompass.repository;

import kh.edu.istad.codecompass.domain.Hint;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HintRepository extends JpaRepository<Hint, Long> {

    List<Hint> findByProblem_Id(long problemId);

    void deleteByProblem_Id(long problemId);

}
