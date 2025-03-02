package team.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import team.backend.domain.Wish;
import team.backend.repository.EventRepository;
import team.backend.repository.WishRepository;

@Service
@RequiredArgsConstructor
public class WishQueryServiceImpl implements WishQueryService{
    private final EventRepository eventRepository;
    private final WishRepository wishRepository;

    @Override
    public Page<Wish> getWishList(Long eventId, Integer page){
        return null;
    }
}
