package kh.edu.istad.codecompass.domain;

import jakarta.persistence.*;
import kh.edu.istad.codecompass.enums.OAuthProvider;
import kh.edu.istad.codecompass.enums.Gender;
import kh.edu.istad.codecompass.enums.Level;
import kh.edu.istad.codecompass.enums.Status;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
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
    private String imageUrl;

    private Level level;
    private Integer coin;
    private Integer star;
    private Long rank;
    private Integer totalProblemsSolved;

    @Column(nullable = false)
    private Boolean isDeleted;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<Problem> problems = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "leader_board_id")
    private LeaderBoard leaderBoard;

    @ManyToMany
    private List<Badge> badges = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<UserProblem> userProblems = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<SubmissionHistories> submissionHistories = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Solution> solutions = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Report> report = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<UserHint>  userHints = new ArrayList<>();

    @OneToOne(mappedBy = "user")
    private CreatorRequest creatorRequest;

    public void updateLevel() {
        this.level = Level.fromStars(this.star);
    }

    @Enumerated(EnumType.STRING)
    private OAuthProvider authProvider; // GOOGLE, GITHUB, NONE

    @Enumerated(EnumType.STRING)
    private Status status = Status.ALLOWED;

}
