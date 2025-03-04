package team.backend.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
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
import team.backend.dto.WishDTO.WishResponseDTO;
import team.backend.repository.EventRepository;
import team.backend.repository.WishRepository;

import java.io.IOException;
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

        Event eventResult = eventRepository.findById(eventId).orElseThrow(EntityNotFoundException::new);

        //크롤링
        WishResponseDTO.WishDto crawData = fetchWishData(request.getLink());

        //컨버터에서 WISH 객체 생성
        Wish newWish = WishConverter.toWish(request, crawData);

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
        WishResponseDTO.WishDto crawData = fetchWishData(request.getLink());

        //수정 name, link, imageurl 순서대로 변수 넣기 / link는 임의로 넣었어요..
        wish.update(crawData.getTitle(), request.getLink(), crawData.getImageUrl());

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

    //위시 조회
    @Override
    @Transactional
    public void checkEvent(Long userId, Long eventId){
        boolean isChecked = eventRepository.isUserCreator(userId, eventId);
        if (!isChecked) {
            throw new EventHandler(ErrorStatus._USER_NOT_CREATE_EVENT);
        }

        boolean isWishExists = wishRepository.existsByEventId(eventId);
        if(!isWishExists) {
            throw new EventHandler(ErrorStatus._EVENT_WISH_NOT_EXIST);
        }
    }

    //크롤링 코드
    @Override
    @Transactional
    public WishResponseDTO.WishDto fetchWishData(String url){
        try {
            // 요청 간 간격을 두기 위해
            Thread.sleep((long)(Math.random() * 15000) + 10000);

            Document doc = Jsoup.connect(url)
                    //.userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36")
                    .timeout(20000)
                    .get();

            // 쿠팡 상품명 및 이미지 크롤링
            String title = doc.select("meta[property=og:title]").attr("content");
            String imageUrl = doc.select("img.prod-image__detail").attr("src");
            //if (imageUrl.startsWith("/")) {
            //    imageUrl = "https://www.coupang.com" + imageUrl;  // 절대 경로로 변환
            //}

            // 상품 정보 반환
            return new WishResponseDTO.WishDto(title, imageUrl);
        } catch (java.net.SocketTimeoutException e) {
            // 타임아웃 발생시 예외 처리
            throw new RuntimeException("타임아웃 발생: " + e.getMessage(), e);
        } catch (IOException e) {
            // 네트워크 오류 발생시 예외 처리
            throw new RuntimeException("네트워크 오류 발생: " + e.getMessage(), e);
        } catch (Exception e) {
            // 다른 예외 처리 (예: InterruptedException)
            throw new RuntimeException("예상치 못한 오류 발생: " + e.getMessage(), e);
        }
    }
}
