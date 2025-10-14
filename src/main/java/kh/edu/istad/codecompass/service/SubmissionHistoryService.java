package kh.edu.istad.codecompass.service;

import kh.edu.istad.codecompass.dto.submissionHistory.response.SubmissionHistoryResponse;

import java.util.List;

public interface SubmissionHistoryService {
    List<SubmissionHistoryResponse> getAllHistory(String username, Long problemId);
}
