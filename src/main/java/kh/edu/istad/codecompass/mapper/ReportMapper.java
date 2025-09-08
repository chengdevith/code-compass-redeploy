package kh.edu.istad.codecompass.mapper;

import kh.edu.istad.codecompass.domain.Report;
import kh.edu.istad.codecompass.dto.report.ReportResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReportMapper {

    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "problemId", source = "problem.id")
    @Mapping(target = "commentId", source = "comment.id")
    ReportResponse toReportResponse(Report report);

}
