package kh.edu.istad.codecompass.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import kh.edu.istad.codecompass.dto.submissionHistory.response.SubmissionHistoryResponse;
import kh.edu.istad.codecompass.service.SubmissionHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/v1/submission-histories")
@RestController
public class SubmissionHistory {

    private final SubmissionHistoryService submissionHistoryService;

    @GetMapping("/{problemId}/problem")
    @Operation(summary = "Get history submission for a subscriber (secured)", security = {@SecurityRequirement(name = "bearerAuth")})
    public List<SubmissionHistoryResponse> getHistory(@AuthenticationPrincipal Jwt jwt, @PathVariable Long problemId) {
        String username = jwt.getClaim("preferred_username");
        return submissionHistoryService.getAllHistory(username, problemId);
    }

}
