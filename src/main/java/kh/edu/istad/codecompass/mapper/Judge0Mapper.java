package kh.edu.istad.codecompass.mapper;

import kh.edu.istad.codecompass.domain.Submission;
import kh.edu.istad.codecompass.dto.jugde0.response.Judge0SubmissionResponse;
import kh.edu.istad.codecompass.dto.jugde0.response.SubmissionResult;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;


@Mapper(componentModel = "spring")
public interface Judge0Mapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "languageId", ignore = true) // Set manually
    @Mapping(target = "status", source = "status.description")
    @Mapping(target = "statusId", source = "status.id")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Submission fromJudge0ResponseToEntity(Judge0SubmissionResponse response);

    @Mapping(target = "memory", source = "memory", qualifiedByName = "mapMemory")
    @Mapping(target = "status", expression = "java(new SubmissionResult.Status(submission.getStatusId(), submission.getStatus()))")
    SubmissionResult fromEntityToResult(Submission submission);

    @Named("mapMemory")
    default String mapMemory(Integer memory) {
        return memory != null ? memory.toString() : null;
    }
}

