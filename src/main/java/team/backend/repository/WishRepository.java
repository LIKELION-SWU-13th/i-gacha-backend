package team.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import team.backend.domain.Event;
import team.backend.domain.User;
import team.backend.domain.Wish;

public interface WishRepository extends JpaRepository<Wish, Long> {
    Page<Wish> findAllByWish(Event event, PageRequest pageRequest);
}
