package kh.edu.istad.codecompass.elasticsearch.service;

import kh.edu.istad.codecompass.elasticsearch.domain.ProblemIndex;
import kh.edu.istad.codecompass.elasticsearch.dto.SearchProblemResponse;
import kh.edu.istad.codecompass.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProblemIndexService {

    List<SearchProblemResponse> searchProblem(String keyword);

    List<ProblemIndex> searchVerifiedProblems(String keyword);

    List<ProblemIndex> searchUnverifiedProblem(String keyword);


}
