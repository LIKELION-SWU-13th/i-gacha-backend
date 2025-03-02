package team.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.backend.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);
}