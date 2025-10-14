package kh.edu.istad.codecompass.mapper;

import kh.edu.istad.codecompass.domain.Solution;
import kh.edu.istad.codecompass.dto.solution.SolutionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SolutionMapper {

    @Mapping(target = "problemId", source = "problem.id")
    @Mapping(target = "author", source = "user.username")
    @Mapping(target = "userProfile", source = "user.imageUrl")
    @Mapping(target = "solutionId", source = "id")
    SolutionResponse toResponse(Solution solution);

}
