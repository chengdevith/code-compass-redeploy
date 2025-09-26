package kh.edu.istad.codecompass.controller;

import kh.edu.istad.codecompass.dto.comment.CommentResponse;
import kh.edu.istad.codecompass.dto.comment.CreateCommentRequest;
import kh.edu.istad.codecompass.dto.report.ChangeStatusRequest;
import kh.edu.istad.codecompass.dto.report.CreateReportRequest;
import kh.edu.istad.codecompass.dto.report.ReportResponse;
import kh.edu.istad.codecompass.service.CommentService;
import kh.edu.istad.codecompass.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/comments")
public class CommentController {

    private final CommentService commentService;
    private final ReportService reportService;
    private final SimpMessagingTemplate messagingTemplate;

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponse createComment(@RequestBody CreateCommentRequest createCommentRequest) {

        CommentResponse commentResponse = commentService.createComment(createCommentRequest);

        // broadcast to topic for this discussion
        messagingTemplate.convertAndSend(
                "/topic/comments." + commentResponse.problemId(), commentResponse
        );

        return commentResponse;
    }

    @PostMapping("/report")
    @ResponseStatus(HttpStatus.CREATED)
    public ReportResponse createReport(@RequestBody CreateReportRequest request) {

        return reportService.createReport(request);

    }

    @PutMapping("/change-status")
    public void changeStatus(@RequestBody ChangeStatusRequest request) {

        reportService.changeStatus(request);

    }

    @GetMapping("/get-report")
    public List<ReportResponse> getReport() {
        return reportService.getReport();
    }

}
