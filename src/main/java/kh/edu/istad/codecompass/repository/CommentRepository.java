package kh.edu.istad.codecompass.repository;

import kh.edu.istad.codecompass.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
