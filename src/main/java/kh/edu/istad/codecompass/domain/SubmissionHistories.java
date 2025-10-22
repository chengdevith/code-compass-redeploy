package kh.edu.istad.codecompass.domain;

import jakarta.persistence.*;
import kh.edu.istad.codecompass.enums.Star;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "submissions_histories")
public class SubmissionHistories {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String code;

    @Column(nullable = false)
    private String languageId;

    @Column(nullable = false)
    private String status;

    private Star star;
    private Integer coin;

    String time;

    Integer memory;

    @Column(nullable = false)
    private LocalDateTime submittedAt;

    @ManyToOne
    private Problem problem;

    @ManyToOne
    private User user;

}
