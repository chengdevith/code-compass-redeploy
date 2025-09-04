package kh.edu.istad.codecompass.elasticsearch.service;

import kh.edu.istad.codecompass.elasticsearch.domain.ProblemIndex;
import kh.edu.istad.codecompass.elasticsearch.repository.ProblemElasticsearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProblemIndexServiceImpl implements ProblemIndexService {

    private final ProblemElasticsearchRepository problemElasticsearchRepository;

    @Override
    public List<ProblemIndex> searchProblem(String keyword) {
        return problemElasticsearchRepository.findByTitleContaining(keyword);
    }
}
