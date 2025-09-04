package kh.edu.istad.codecompass.elasticsearch.repository;


import kh.edu.istad.codecompass.elasticsearch.domain.ProblemIndex;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface ProblemElasticsearchRepository extends ElasticsearchRepository<ProblemIndex, String> {

    List<ProblemIndex> findByTitleContaining(String keyword);

}
