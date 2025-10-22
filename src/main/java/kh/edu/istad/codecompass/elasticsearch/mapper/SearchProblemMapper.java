package kh.edu.istad.codecompass.elasticsearch.mapper;

import kh.edu.istad.codecompass.elasticsearch.domain.ProblemIndex;
import kh.edu.istad.codecompass.elasticsearch.dto.SearchProblemResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SearchProblemMapper {

    @Mapping(source = "problemId", target = "id")
    SearchProblemResponse fromEntityToResponse(ProblemIndex entity);

}
