package kh.edu.istad.codecompass.repository;

import kh.edu.istad.codecompass.domain.Solution;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SolutionRepository extends CrudRepository<Solution, Long> {

    List<Solution> findByIsDeletedFalse();

    List<Solution> findSolutionByProblemIdAndIsDeletedFalse(Long problemId);

}
