package kh.edu.istad.codecompass.elasticsearch.service;

import kh.edu.istad.codecompass.elasticsearch.domain.ProblemIndex;
import kh.edu.istad.codecompass.elasticsearch.dto.SearchProblemResponse;
import kh.edu.istad.codecompass.elasticsearch.mapper.SearchProblemMapper;
import kh.edu.istad.codecompass.elasticsearch.repository.ProblemElasticsearchRepository;
import kh.edu.istad.codecompass.enums.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHitSupport;
import org.springframework.stereotype.Service;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;

import java.util.List;


@Service
@RequiredArgsConstructor
public class ProblemIndexServiceImpl implements ProblemIndexService {

    private final ProblemElasticsearchRepository problemElasticsearchRepository;
    private final ElasticsearchOperations operations;
    private final SearchProblemMapper mapper;

    @Override
    public List<SearchProblemResponse> searchProblem(String keyword) {
        return problemElasticsearchRepository.findByTitleContaining(keyword).stream().map(
                mapper::fromEntityToResponse
        ).toList();
    }

    @Override
    public List<ProblemIndex> searchVerifiedProblems(String keyword) {
//        NativeQuery query = new NativeQueryBuilder()
//                .withQuery(q -> q.bool(b -> b
//                        .must(m -> m.match(t -> t.field("title").query(keyword)))
//                        .must(m -> m.term(t -> t.field("isDeleted").value(false)))
//                        .must(m -> m.term(t -> t.field("status").value("APPROVED")))
//                ))
//                .withPageable(pageable)
//                .build();
//
//        SearchHits<ProblemIndex> hits = operations.search(query, ProblemIndex.class);
//        return SearchHitSupport.searchPageFor(hits, pageable).map(SearchHit::getContent);
        return problemElasticsearchRepository.findByTitleContainingAndIsDeletedFalseAndStatus(keyword, Status.APPROVED);
    }

    @Override
    public List<ProblemIndex> searchUnverifiedProblem(String keyword) {
        return problemElasticsearchRepository.findByTitleContainingAndIsDeletedFalseAndStatus(keyword, Status.PENDING);
    }
}
