package kh.edu.istad.codecompass.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "medias")
public class Media {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 155)
    private String name;

    @Column(nullable = false, length = 10)
    private String extension;

    @Column(nullable = false, length = 10)
    private String mineTypeFile;

    @Column(nullable = false)
    private Boolean isDeleted;
}
