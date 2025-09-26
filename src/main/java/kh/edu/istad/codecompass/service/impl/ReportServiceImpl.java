package kh.edu.istad.codecompass.service.impl;

import kh.edu.istad.codecompass.domain.Report;
import kh.edu.istad.codecompass.dto.report.ChangeStatusRequest;
import kh.edu.istad.codecompass.dto.report.CreateReportRequest;
import kh.edu.istad.codecompass.dto.report.ReportResponse;
import kh.edu.istad.codecompass.enums.ReportStatus;
import kh.edu.istad.codecompass.mapper.ReportMapper;
import kh.edu.istad.codecompass.repository.CommentRepository;
import kh.edu.istad.codecompass.repository.ProblemRepository;
import kh.edu.istad.codecompass.repository.ReportRepository;
import kh.edu.istad.codecompass.repository.UserRepository;
import kh.edu.istad.codecompass.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final ProblemRepository problemRepository;
    private final ReportMapper reportMapper;

    @Override
    public ReportResponse createReport(CreateReportRequest request) {

        Report report = new Report();
        report.setReason(request.reason());
        report.setCreateAt(LocalDateTime.now());
        report.setStatus(ReportStatus.PENDING);

        report.setUser(userRepository.findUserByUsername(request.username()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
        ));

        report.setComment(commentRepository.findById(request.commentId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found")
        ));

        report.setProblem(problemRepository.findById(request.problemId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Problem not found")
        ));

        reportRepository.save(report);

        return reportMapper.toReportResponse(report);
    }

    @Override
    public void changeStatus(ChangeStatusRequest request) {

        Report report = reportRepository.findById(request.id()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Report not found")
        );

        report.setStatus(request.status());

        reportRepository.save(report);

    }

    @Override
    public List<ReportResponse> getReport() {
        return reportMapper.toReportResponseList(reportRepository.findAll());
    }

}
