package kh.edu.istad.codecompass.repository;

import kh.edu.istad.codecompass.domain.Solution;
import org.hibernate.dialect.lock.OptimisticEntityLockException;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface SolutionRepository extends CrudRepository<Solution, Long> {

    List<Solution> findByIsDeletedFalse();

    List<Solution> findSolutionByProblemIdAndIsDeletedFalse(Long problemId);

    Optional<Solution> findSolutionByIdAndUser_Username(Long solutionId, String username);

}
