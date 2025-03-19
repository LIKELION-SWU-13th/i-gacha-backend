package team.backend.service;

import org.springframework.data.domain.Page;
import team.backend.domain.Event;
import team.backend.domain.Wish;

import java.util.List;
import java.util.Optional;

public interface WishQueryService {
//    Optional<Event> findEvent(Long id);
    List<Wish> getWishList(Long eventId);
    Event getEventName(Long eventId);
    Wish drawRandomWish(Long eventId);
}
