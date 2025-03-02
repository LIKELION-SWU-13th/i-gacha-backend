package team.backend.service;

import team.backend.domain.Wish;
import team.backend.dto.WishDTO.WishRequestDTO;

public interface WishCommandService {
    Wish joinEvent(Long userId, Long eventId, WishRequestDTO.CreateRqDTO request);
    void delSerWish(Long userId, Long eventId, Long wishId);
    Wish updateWish(Long userId, Long eventId, Long wishId, WishRequestDTO.CreateRqDTO request);
    void checkEvent(Long userId, Long eventId);
}
