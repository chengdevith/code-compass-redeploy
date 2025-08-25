package kh.edu.istad.codecompass.repository;

import kh.edu.istad.codecompass.domain.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {

    Optional<User> findUserByUsername(String username);
}
