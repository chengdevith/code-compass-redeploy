package kh.edu.istad.codecompass.repository;

import kh.edu.istad.codecompass.domain.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {
}
