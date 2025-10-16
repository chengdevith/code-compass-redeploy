package kh.edu.istad.codecompass.repository;

import kh.edu.istad.codecompass.domain.Solution;
import org.hibernate.dialect.lock.OptimisticEntityLockException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface SolutionRepository extends CrudRepository<Solution, Long> {

    List<Solution> findByIsDeletedFalse();

    Page<Solution> findSolutionByProblemIdAndIsDeletedFalse(Long problemId, Pageable pageable);

    Optional<Solution> findSolutionByIdAndUser_Username(Long solutionId, String username);

}
