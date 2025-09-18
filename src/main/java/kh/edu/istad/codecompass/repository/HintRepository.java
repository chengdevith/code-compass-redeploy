package kh.edu.istad.codecompass.repository;

import kh.edu.istad.codecompass.domain.Hint;
import kh.edu.istad.codecompass.domain.Problem;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface HintRepository extends CrudRepository<Hint, Long> {

    List<Hint> findByProblem_Id(long problemId);

}
