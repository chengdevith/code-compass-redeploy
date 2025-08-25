package kh.edu.istad.codecompass.mapper;

import kh.edu.istad.codecompass.domain.Hint;
import kh.edu.istad.codecompass.domain.Problem;
import kh.edu.istad.codecompass.domain.Tag;
import kh.edu.istad.codecompass.domain.TestCase;
import kh.edu.istad.codecompass.dto.HintResponse;
import kh.edu.istad.codecompass.dto.TestCaseRequest;
import kh.edu.istad.codecompass.dto.TestCaseResponse;
import kh.edu.istad.codecompass.dto.problem.CreateProblemRequest;
import kh.edu.istad.codecompass.dto.problem.ProblemResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface ProblemMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tags", ignore = true) // handled manually in service
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

    // custom helpers
    default TestCase toTestCase(TestCaseRequest request, Problem problem) {
        TestCase tc = new TestCase();
        tc.setInput(request.input());
        tc.setExpectedOutput(request.expectedOutput());
        tc.setProblem(problem);
        return tc;
    }

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
        return hints.stream().map(hint -> new HintResponse(hint.getDescription(), hint.getIsLocked()))
                .toList();
    }

}

