package team.backend.service;

import org.springframework.data.domain.Page;
import team.backend.domain.Event;
import team.backend.domain.Wish;

import java.util.Optional;

public interface WishQueryService {
//    Optional<Event> findEvent(Long id);
    Page<Wish> getWishList(Long eventId, Integer page);
    Event getEventName(Long eventId);
    Wish drawRandomWish(Long eventId);
}
