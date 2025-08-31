package kh.edu.istad.codecompass.dto.packageDTO;

import kh.edu.istad.codecompass.dto.problem.response.ProblemSummaryResponse;
import lombok.Builder;

import java.util.List;

@Builder
public record PackageResponse(
        String name,
        String description,
        List<ProblemSummaryResponse> problems
) { }
