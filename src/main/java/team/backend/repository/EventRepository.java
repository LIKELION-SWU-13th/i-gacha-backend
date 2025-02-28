package team.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.backend.domain.Event;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByUserId(Long userId);
    Optional<Event> findByUserIdAndId(Long userId, Long eventId);
}