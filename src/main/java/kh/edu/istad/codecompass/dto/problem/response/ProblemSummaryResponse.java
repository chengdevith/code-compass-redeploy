package kh.edu.istad.codecompass.dto.problem.response;

import kh.edu.istad.codecompass.enums.Difficulty;

import java.util.List;

public record ProblemSummaryResponse(
        Long id,
        String title,
        Difficulty difficulty,
        List<String> tags
) { }
