package kh.edu.istad.codecompass.elasticsearch.service;

import kh.edu.istad.codecompass.elasticsearch.domain.ProblemIndex;
import kh.edu.istad.codecompass.elasticsearch.repository.ProblemElasticsearchRepository;
import kh.edu.istad.codecompass.enums.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProblemIndexServiceImpl implements ProblemIndexService {

    private final ProblemElasticsearchRepository problemElasticsearchRepository;

    @Override
    public Page<ProblemIndex> searchProblem(String keyword, Pageable pageable) {
        return problemElasticsearchRepository.findByTitleContaining(keyword, pageable);
    }

    @Override
    public Page<ProblemIndex> searchVerifiedProblems(String keyword, Pageable pageable) {
        return problemElasticsearchRepository.findByTitleContainingAndIsDeletedFalseAndStatus(keyword, Status.APPROVED, pageable);
    }

    @Override
    public Page<ProblemIndex> searchUnverifiedProblem(String keyword, Pageable pageable) {
        return problemElasticsearchRepository.findByTitleContainingAndIsDeletedFalseAndStatus(keyword, Status.PENDING, pageable);
    }
}
