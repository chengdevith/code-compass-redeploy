package kh.edu.istad.codecompass.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "user_hints")
public class UserHint {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "hint_id")
    private Hint hint;

    Boolean isUnlocked = false;

    public UserHint(User user, Hint hint, boolean isUnlocked) {
        this.user = user;
        this.hint = hint;
        this.isUnlocked = isUnlocked;
    }
}



