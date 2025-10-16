package kh.edu.istad.codecompass.elasticsearch.service;

import kh.edu.istad.codecompass.elasticsearch.domain.ProblemIndex;
import kh.edu.istad.codecompass.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProblemIndexService {

    Page<ProblemIndex> searchProblem(String keyword, Pageable pageable);

    Page<ProblemIndex> searchVerifiedProblems(String keyword, Pageable pageable);

    Page<ProblemIndex> searchUnverifiedProblem(String keyword, Pageable pageable);


}
