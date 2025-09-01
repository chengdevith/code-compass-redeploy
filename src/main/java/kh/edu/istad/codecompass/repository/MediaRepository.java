package kh.edu.istad.codecompass.repository;

import kh.edu.istad.codecompass.domain.Media;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MediaRepository extends JpaRepository<Media,Long> {

    Optional<Media> findByNameAndIsDeletedFalse(String name);

}
