package kh.edu.istad.codecompass.elasticsearch.repository;


import kh.edu.istad.codecompass.elasticsearch.domain.ProblemIndex;
import kh.edu.istad.codecompass.enums.Status;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;
import java.util.Optional;

public interface ProblemElasticsearchRepository extends ElasticsearchRepository<ProblemIndex, String> {

    Page<ProblemIndex> findByTitleContaining(String keyword, Pageable pageable);

    Optional<ProblemIndex> findProblemIndexById(String problemId);

    Page<ProblemIndex> findByTitleContainingAndIsDeletedFalse(String keyword, Pageable pageable);

    Page<ProblemIndex> findByTitleContainingAndIsDeletedFalseAndStatus(String keyword, Status status, Pageable pageable);

}
