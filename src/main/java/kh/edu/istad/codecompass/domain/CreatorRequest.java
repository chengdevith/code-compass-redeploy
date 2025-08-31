package kh.edu.istad.codecompass.domain;

import jakarta.persistence.*;
import kh.edu.istad.codecompass.enums.ReportStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "creator_requests")
public class CreatorRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    String description;

    ReportStatus status;

    @OneToMany(mappedBy = "creatorRequest", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    List<User> users = new ArrayList<>();

}
