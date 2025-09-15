package kh.edu.istad.codecompass.mapper;

import kh.edu.istad.codecompass.domain.Hint;
import kh.edu.istad.codecompass.domain.Problem;
import kh.edu.istad.codecompass.domain.Tag;
import kh.edu.istad.codecompass.domain.TestCase;
import kh.edu.istad.codecompass.dto.hint.response.HintResponse;
import kh.edu.istad.codecompass.dto.problem.request.UpdateProblemRequest;
import kh.edu.istad.codecompass.dto.problem.response.ProblemSummaryResponse;
import kh.edu.istad.codecompass.dto.testCase.TestCaseRequest;
import kh.edu.istad.codecompass.dto.testCase.TestCaseResponse;
import kh.edu.istad.codecompass.dto.problem.request.CreateProblemRequest;
import kh.edu.istad.codecompass.dto.problem.response.ProblemResponse;
import org.mapstruct.*;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface ProblemMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tags", ignore = true)
    @Mapping(target = "createAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updateAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "isVerified", constant = "false")
    @Mapping(target = "isDeleted", constant = "false")
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "hints", ignore = true)
    Problem fromRequestToEntity(CreateProblemRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "problem", ignore = true)
    @Mapping(target = "expectedOutput", source = "expectedOutput")
    TestCase toTestCase(TestCaseRequest request);

    @Mapping(target = "testCases", expression = "java(mapTestCases(entity.getTestCases()))")
    @Mapping(target = "tags", expression = "java(mapTags(entity.getTags()))")
    @Mapping(target = "author", source = "author.username")
    @Mapping(target = "hints", expression = "java(mapHints(entity.getHints()))")
    ProblemResponse fromEntityToResponse(Problem entity);

    @Named("toSummary")
    @Mapping(target = "tags", expression = "java(mapTags(entity.getTags()))")
    ProblemSummaryResponse fromEntityToSummaryResponse(Problem entity);
    
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void fromUpdateRequestToEntity(UpdateProblemRequest request, @MappingTarget Problem entity);

    // custom helpers

    default List<TestCaseResponse> mapTestCases(List<TestCase> testCases) {
        if (testCases == null) return List.of();
        return testCases.stream()
                .map(tc -> new TestCaseResponse(tc.getInput(), tc.getExpectedOutput()))
                .toList();
    }

    default List<String> mapTags(Set<Tag> tags) {
        if (tags == null) return List.of();
        return tags.stream()
                .map(Tag::getTagName)
                .toList();
    }

    default List<HintResponse> mapHints(List<Hint> hints) {
        if (hints == null) return List.of();
        return hints.stream().map(hint -> new HintResponse(hint.getId(), hint.getDescription(), hint.getIsLocked()))
                .toList();
    }

}

