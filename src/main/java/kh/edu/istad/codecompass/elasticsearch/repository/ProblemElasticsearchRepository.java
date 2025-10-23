package kh.edu.istad.codecompass.elasticsearch.repository;


import kh.edu.istad.codecompass.elasticsearch.domain.ProblemIndex;
import kh.edu.istad.codecompass.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;
import java.util.Optional;

public interface ProblemElasticsearchRepository extends ElasticsearchRepository<ProblemIndex, String> {

    List<ProblemIndex> findByTitleContaining(String keyword);

    Optional<ProblemIndex> findProblemIndexById(String problemId);

    List<ProblemIndex> findByTitleContainingAndIsDeletedFalse(String keyword, Pageable pageable);

    List<ProblemIndex> findByTitleContainingAndIsDeletedFalseAndStatus(String keyword, Status status);

    Optional<ProblemIndex> findProblemIndexByProblemId(Long problemId);

}
