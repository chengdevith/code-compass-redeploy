package kh.edu.istad.codecompass.elasticsearch.repository;

import kh.edu.istad.codecompass.elasticsearch.domain.UserIndex;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface UserElasticsearchRepository extends ElasticsearchRepository<UserIndex, String> {

    List<UserIndex> findByUsernameContaining(String keyword);

    List<UserIndex> findByLevel(String level);

    List<UserIndex> findByRank(Long rank);

}
