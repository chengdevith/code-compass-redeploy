package kh.edu.istad.codecompass.domain;

import jakarta.persistence.*;
import kh.edu.istad.codecompass.enums.Difficulty;
import kh.edu.istad.codecompass.enums.Star;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "problems")
public class Problem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(nullable = false)
    private String description;

    @Column(nullable = false, length = 100, unique = true)
    private String title;

    @Column(nullable = false)
    private Difficulty difficulty;

    @Column(nullable = false)
    private Integer coin;

    @Column(nullable = false)
    private Star star;

    @Column(nullable = false)
    private Double bestTimeExecution;

    @Column(nullable = false)
    private Integer bestMemoryUsage;

    @Column(nullable = false)
    private LocalDateTime createAt;

    @Column(nullable = false)
    private LocalDateTime updateAt;

    @Column(nullable = false)
    private Boolean isLocked;

    @Column(nullable = false)
    private Boolean isDeleted;

    @ManyToMany(mappedBy = "problems")
    private Set<Package> packages;

    @OneToMany(mappedBy = "problem")
    private List<UserProblem> userProblems;

    @OneToMany(mappedBy = "problem")
    private Queue<SubmissionHistories> submissionHistories;

    @OneToMany(mappedBy = "problem")
    private List<Solution>  solutions;

    @OneToMany(mappedBy = "problem")
    private List<TestCase> testCases;

    @ManyToMany
    @JoinTable(
            name = "problem_tags",
            joinColumns = @JoinColumn(name = "problem_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

    @OneToMany(mappedBy = "problem")
    private List<Report> reports;
}
