package kh.edu.istad.codecompass.domain;

import jakarta.persistence.*;
import kh.edu.istad.codecompass.enums.Gender;
import kh.edu.istad.codecompass.enums.Level;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;

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

    @Column(nullable = false, length = 10)
    private String password;

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
    private Integer coins;
    private Integer stars;
    private Integer rank;
    private Integer total_problems_solved;

    @Column(nullable = false)
    private Boolean isDeleted;

    @ManyToOne
    private LeaderBoard leaderBoard;

    @ManyToMany
    private List<Badge> badges;

    @OneToMany(mappedBy = "user")
    private Set<UserProblem> userProblems;
}
