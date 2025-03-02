package team.backend.service;

import org.springframework.data.domain.Page;
import team.backend.domain.Wish;

import java.util.Optional;

public interface WishQueryService {
    Optional<Wish> findWish(Long id);
    Page<Wish> getWishList(Long eventId, Integer page);
}
