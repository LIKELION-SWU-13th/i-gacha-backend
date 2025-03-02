package team.backend.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import team.backend.apiPayload.code.status.ErrorStatus;
import team.backend.apiPayload.exception.handler.EventHandler;
import team.backend.converter.WishConverter;
import team.backend.domain.Event;
import team.backend.domain.Wish;
import team.backend.dto.WishDTO.WishRequestDTO;
import team.backend.repository.EventRepository;
import team.backend.repository.WishRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WishCommandServiceImpl implements WishCommandService {

    private final WishRepository wishRepository;
    private final EventRepository eventRepository;

    //위시 생성
    @Override
    @Transactional
    public Wish joinEvent(Long userId, Long eventId, WishRequestDTO.CreateRqDTO request){
        // 해당 사용자가 만든 이벤트가 맞는지 검사
        boolean isChecked = eventRepository.isUserCreator(userId, eventId);
        if (!isChecked) {
            throw new EventHandler(ErrorStatus._USER_NOT_CREATE_EVENT);
        }

        // 크롤링 단계

        Event eventResult = eventRepository.findById(eventId).orElseThrow(EntityNotFoundException::new);

        //컨버터에서 WISH 객체 생성
        Wish newWish = WishConverter.toWish(request);

        //양방향 매핑
        newWish.setEvent(eventResult);

        wishRepository.save(newWish);

        return newWish;
    }

    //위시 수정
    @Override
    @Transactional
    public Wish updateWish(Long userId, Long eventId, Long wishId, WishRequestDTO.CreateRqDTO request){
        //USER > EVENT > WISH 검사
        int isChecked = eventRepository.existsWishForUser(userId, eventId, wishId);
        if (isChecked != 1) {
            throw new EventHandler(ErrorStatus._USER_EVENT_WISH);
        }

        Wish wish = wishRepository.findById(wishId)
                .orElseThrow(() -> new EventHandler(ErrorStatus._WISH_NOT_FOUND));

        //크롤링

        //수정 name, link, imageurl 순서대로 변수 넣기 / link는 임의로 넣었어요..
        wish.update(request.getLink(), request.getLink(), request.getLink());

        wishRepository.save(wish);

        return wish;
    }

    //위시 삭제
    @Override
    @Transactional
    public void delSerWish(Long userId, Long eventId, Long wishId){
        //USER > EVENT > WISH 검사
        int isChecked = eventRepository.existsWishForUser(userId, eventId, wishId);
        if (isChecked != 1) {
            throw new EventHandler(ErrorStatus._USER_EVENT_WISH);
        }

        //위시 삭제
        wishRepository.deleteById(wishId);
    }
}
