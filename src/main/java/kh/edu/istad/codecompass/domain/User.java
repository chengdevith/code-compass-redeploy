package kh.edu.istad.codecompass.domain;

import jakarta.persistence.*;
import kh.edu.istad.codecompass.enums.Gender;
import kh.edu.istad.codecompass.enums.Level;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 150)
    private String username;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false)
    private Gender gender;

    private String dob;

    private String location;
    private String website;
    private String github;
    private String linkedin;
    private String image_url;

    private Level level;
    private Integer coin;
    private Integer star;
    private Integer rank;
    private Integer total_problems_solved;

    @Column(nullable = false)
    private Boolean isDeleted;

    @ManyToOne
    private LeaderBoard leaderBoard;

    @ManyToMany
    private List<Badge> badges = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<UserProblem> userProblems = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<SubmissionHistories> submissionHistories = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Solution> solutions = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Report> report = new ArrayList<>();
}
