package kh.edu.istad.codecompass.elasticsearch.service;

import kh.edu.istad.codecompass.elasticsearch.domain.ProblemIndex;

import java.util.List;

public interface ProblemIndexService {

    List<ProblemIndex> searchProblem(String keyword);

}
