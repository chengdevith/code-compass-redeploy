package kh.edu.istad.codecompass.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "hints")
public class Hint {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(nullable = false)
    private Boolean isLocked;

    @ManyToOne
    Problem problem;

    @OneToMany(mappedBy = "hint", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<UserHint> userHints = new ArrayList<>();
}
