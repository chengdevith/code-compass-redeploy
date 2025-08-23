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
@Table(name = "discussions")
public class Discussion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "discussion", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Comment> comments = new ArrayList<>();

    @OneToOne
    private Problem problem;
}
