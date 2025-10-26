package kh.edu.istad.codecompass.mapper;


import kh.edu.istad.codecompass.domain.Package;
import kh.edu.istad.codecompass.domain.Problem;
import kh.edu.istad.codecompass.domain.Tag;
import kh.edu.istad.codecompass.dto.packageDTO.request.PackageRequest;
import kh.edu.istad.codecompass.dto.packageDTO.response.PackageResponse;
import kh.edu.istad.codecompass.dto.packageDTO.response.PackageSummaryResponse;
import kh.edu.istad.codecompass.dto.problem.response.ProblemSummaryResponse;
import org.mapstruct.*;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = { ProblemMapper.class, BadgeMapper.class })
public interface PackageMapper {

    @Mapping(target = "problems", source = "problems")
    @Mapping(target = "badgesResponse", source = "badge")
    @Mapping(target = "totalProblems", expression = "java(pack.getProblems().size())")
    PackageResponse mapPackageToResponse(Package pack);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updatePackagePartially(PackageRequest packageRequest, @MappingTarget Package pack);

    List<ProblemSummaryResponse> toProblemSummaryResponses(List<Problem> problems);

    @Mapping(target = "totalProblems", expression = "java(packages.getProblems().stream().filter(p -> !p.getIsDeleted()).count())")
    @Mapping(target = "badgesResponse", source = "badge")
    PackageSummaryResponse toPackageSummaryResponses(Package packages);


    default ProblemSummaryResponse toProblemSummaryResponse(Problem problem) {
        return new ProblemSummaryResponse(
                problem.getId(),
                problem.getTitle(),
                problem.getDifficulty(),
                problem.getTags().stream().map(Tag::getTagName).collect(Collectors.toList())
        );
    }

}

