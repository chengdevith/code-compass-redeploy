package kh.edu.istad.codecompass.repository;

import kh.edu.istad.codecompass.domain.Discussion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiscussionRepository extends JpaRepository<Discussion,Long> {
}
