package team.backend.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import team.backend.apiPayload.code.status.ErrorStatus;
import team.backend.apiPayload.exception.handler.EventHandler;
import team.backend.domain.Event;
import team.backend.domain.Wish;
import team.backend.repository.EventRepository;
import team.backend.repository.WishRepository;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class WishQueryServiceImpl implements WishQueryService{
    private final EventRepository eventRepository;
    private final WishRepository wishRepository;

    @Override
    public Page<Wish> getWishList(Long eventId, Integer page){
        Event event = eventRepository.findById(eventId).get();
        Page<Wish> EventPage = wishRepository.findAllByEvent(event, PageRequest.of(page, 10));
        return EventPage;
    }

    @Override
    public Event getEventName(Long eventId){
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventHandler(ErrorStatus._EVENT_NOT_FOUND));
        return event;
    }

    @Override
    public Wish drawRandomWish(Long eventId){
        //모든 wish 목록
        List<Wish> wishList = wishRepository.findByEventId(eventId);

        if (wishList.isEmpty()) {
            throw new EventHandler(ErrorStatus._EVENT_WISH_NOT_EXIST);
        }

        Long minId = wishRepository.findMinIdByEvent(eventId);
        Long maxId = wishRepository.findMaxIdByEvent(eventId);

        if (minId == null || maxId == null) {
            throw new EntityNotFoundException("해당 이벤트에 등록된 위시가 없습니다.");
        }

        // 랜덤한 ID 생성
        Random random = new Random();
        Long randomId;
        Wish wish = null;

        while (wish == null) {
            randomId = minId + random.nextInt((int) (maxId - minId + 1)); // minId ~ maxId 사이의 랜덤 값
            wish = wishRepository.findById(randomId).orElse(null);
        }

        return wish;

    }
}
