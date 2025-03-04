package team.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import team.backend.domain.Event;
import team.backend.domain.Wish;

import java.util.List;

public interface WishRepository extends JpaRepository<Wish, Long> {
    Page<Wish> findAllByEvent(Event event, PageRequest pageRequest);
    List<Wish> findByEventId(Long eventId);

    @Query("SELECT MIN(w.id) FROM Wish w WHERE w.event.id = :eventId")
    Long findMinIdByEvent(@Param("eventId") Long eventId);

    @Query("SELECT MAX(w.id) FROM Wish w WHERE w.event.id = :eventId")
    Long findMaxIdByEvent(@Param("eventId") Long eventId);

    boolean existsByEventId(Long eventId);
}
