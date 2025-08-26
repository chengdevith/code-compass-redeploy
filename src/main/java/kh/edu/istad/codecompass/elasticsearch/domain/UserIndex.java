/**
 In Elasticsearch We don’t put all fields (like passwords, coins, isDeleted, relationships).
 Instead, only fields useful for searching/filtering.
 */

package kh.edu.istad.codecompass.elasticsearch.domain;

import org.springframework.data.annotation.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(indexName = "users") // Elasticsearch index (like a table)
public class UserIndex {
    @Id
    private String id;          // same as Postgres User.id

    private String username;
    private String email;
    private String gender;

    private String location;
    private String github;
    private String linkedin;
    private String imageUrl;

    private String level;
    private Integer rank;

    private Integer totalProblemsSolved;
}
