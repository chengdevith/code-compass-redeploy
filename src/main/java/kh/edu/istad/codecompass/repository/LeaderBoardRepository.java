package kh.edu.istad.codecompass.repository;

import kh.edu.istad.codecompass.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeaderBoardRepository extends JpaRepository<User, Long> {


}
