package kh.edu.istad.codecompass.repository;

import kh.edu.istad.codecompass.domain.TestCase;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestCaseRepository extends JpaRepository<TestCase, Long> {
}
