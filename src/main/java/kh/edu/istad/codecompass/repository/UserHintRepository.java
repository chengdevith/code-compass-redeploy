package kh.edu.istad.codecompass.repository;

import kh.edu.istad.codecompass.domain.Hint;
import kh.edu.istad.codecompass.domain.Problem;
import kh.edu.istad.codecompass.domain.User;
import kh.edu.istad.codecompass.domain.UserHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserHintRepository extends JpaRepository<UserHint, Long> {

    Optional<UserHint> findByUserAndHint(User user, Hint hint);

    @Query("SELECT uh FROM UserHint uh WHERE uh.user = :user AND uh.hint.problem = :problem")
    List<UserHint> findByUserAndHintProblem(@Param("user") User user, @Param("problem") Problem problem);

    void removeUserHintByHint_Id(Long hintId);
//    List<UserHint> findUserHintByHint_Id(Long hintId);
}
