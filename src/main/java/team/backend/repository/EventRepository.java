package team.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import team.backend.domain.Event;
import team.backend.domain.User;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findAllByUser(User user);
    Optional<Event> findByUserAndId(User user, Long eventId);

    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM Event e WHERE e.id = :eventId AND e.user.id = :userId")
    Boolean isUserCreator(@Param("userId") Long userId, @Param("eventId") Long eventId);

    @Query(value = "SELECT EXISTS ( " +
            "    SELECT 1 FROM wish w " +
            "    JOIN event e ON w.event_id = e.id " +
            "    JOIN user u ON e.user_id = u.id " +
            "    WHERE u.id = :userId AND e.id = :eventId AND w.id = :wishId" +
            ") AS tr",
            nativeQuery = true)
    int existsWishForUser(@Param("userId") Long userId,
                          @Param("eventId") Long eventId,
                          @Param("wishId") Long wishId);
}
