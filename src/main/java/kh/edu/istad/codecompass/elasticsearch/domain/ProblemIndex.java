package kh.edu.istad.codecompass.elasticsearch.domain;

import jakarta.persistence.Id;
import kh.edu.istad.codecompass.enums.Difficulty;
import kh.edu.istad.codecompass.enums.Star;
import kh.edu.istad.codecompass.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(indexName = "problem") // Elasticsearch index (like a table)
public class ProblemIndex {
    @Id
    private String id;

    private String description;

    private String title;

    private Difficulty difficulty;

    private Integer coin;

    private Star star;

    private Double bestTimeExecution;

    private Status status = Status.PENDING;

    private Boolean isDeleted;

    private Integer bestMemoryUsage;

    private Long authorId;
    private String authorUsername;
}
