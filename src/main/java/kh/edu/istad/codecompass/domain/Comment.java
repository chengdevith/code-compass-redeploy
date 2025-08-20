package kh.edu.istad.codecompass.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String comment;

    @Column(nullable = false)
    private LocalDateTime commentAt;

    @Column(nullable = false)
    private Boolean isDeleted;

    @ManyToOne
    private User user;

    @ManyToOne
    private Discussion discussion;

    @OneToMany(mappedBy = "comment")
    private List<Report> report;
}
