package kh.edu.istad.codecompass.dto.problem.response;

import kh.edu.istad.codecompass.enums.Difficulty;
import lombok.Builder;

import java.util.List;

@Builder
public record ProblemSummaryResponse(
        Long id,
        String title,
        Difficulty difficulty,
        List<String> tags
) { }
