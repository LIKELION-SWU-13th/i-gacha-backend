package team.backend.service;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitUntilState;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import team.backend.apiPayload.code.status.ErrorStatus;
import team.backend.apiPayload.exception.handler.EventHandler;
import team.backend.domain.Event;
import team.backend.domain.Wish;
import team.backend.dto.WishDTO.WishRequestDTO;
import team.backend.repository.EventRepository;
import team.backend.repository.WishRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class WishCommandServiceImpl implements WishCommandService {

    private final WishRepository wishRepository;
    private final EventRepository eventRepository;

    // 위시 생성
    @Override
    @Transactional
    public Wish joinEvent(Long userId, Long eventId, WishRequestDTO.CreateRqDTO request){
        // 해당 사용자가 만든 이벤트가 맞는지 검사
        boolean isChecked = eventRepository.isUserCreator(userId, eventId);
        if (!isChecked) {
            throw new EventHandler(ErrorStatus._USER_NOT_CREATE_EVENT);
        }

        Event eventResult = eventRepository.findById(eventId).orElseThrow(EntityNotFoundException::new);

        // 크롤링
        Map<String, String> crawData = fetchWishData(request.getLink());

        // 크롤링한 데이터로 Wish 객체 생성
        Wish newWish = new Wish();
        newWish.setName(crawData.get("title"));
        newWish.setLink(request.getLink());
        newWish.setImageUrl(crawData.get("image"));

        // 양방향 매핑
        newWish.setEvent(eventResult);

        wishRepository.save(newWish);

        return newWish;
    }

    // 위시 수정
    @Override
    @Transactional
    public Wish updateWish(Long userId, Long eventId, Long wishId, WishRequestDTO.CreateRqDTO request){
        // USER > EVENT > WISH 검사
        int isChecked = eventRepository.existsWishForUser(userId, eventId, wishId);
        if (isChecked != 1) {
            throw new EventHandler(ErrorStatus._USER_EVENT_WISH);
        }

        Wish wish = wishRepository.findById(wishId)
                .orElseThrow(() -> new EventHandler(ErrorStatus._WISH_NOT_FOUND));

        // 크롤링
        Map<String, String> crawData = fetchWishData(request.getLink());

        // 수정 name, link, imageurl 순서대로 변수 넣기
        wish.update(crawData.get("title"), request.getLink(), crawData.get("image"));

        wishRepository.save(wish);

        return wish;
    }

    // 위시 삭제
    @Override
    @Transactional
    public void delSerWish(Long userId, Long eventId, Long wishId){
        // USER > EVENT > WISH 검사
        int isChecked = eventRepository.existsWishForUser(userId, eventId, wishId);
        if (isChecked != 1) {
            throw new EventHandler(ErrorStatus._USER_EVENT_WISH);
        }

        // 위시 삭제
        wishRepository.deleteById(wishId);
    }

    // 위시 조회
    @Override
    @Transactional
    public void checkEvent(Long userId, Long eventId){
        boolean isChecked = eventRepository.isUserCreator(userId, eventId);
        if (!isChecked) {
            throw new EventHandler(ErrorStatus._USER_NOT_CREATE_EVENT);
        }

        boolean isWishExists = wishRepository.existsByEventId(eventId);
        if (!isWishExists) {
            throw new EventHandler(ErrorStatus._EVENT_WISH_NOT_EXIST);
        }
    }

    // Playwright 기반 크롤링 코드
    public Map<String, String> fetchWishData(String url) {
        Map<String, String> productData = new HashMap<>();

        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                    .setHeadless(true)
                    .setArgs(List.of(
                            "--disable-blink-features=AutomationControlled",  // 자동화 감지 방지
                            "--no-sandbox",
                            "--disable-gpu"
                    ))
            );

            // HTTP/1.1 강제 + User-Agent 조작
            BrowserContext context = browser.newContext(new Browser.NewContextOptions()
                    .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/110.0.0.0 Safari/537.36")
                    .setExtraHTTPHeaders(Map.of(
                            "accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8",
                            "accept-encoding", "gzip, deflate",  // HTTP/1.1 강제
                            "accept-language", "en-US,en;q=0.5"
                    ))
            );

            Page page = context.newPage();

            // 자동화 감지 방지
            page.addInitScript("Object.defineProperty(navigator, 'webdriver', {get: () => undefined})");

            // 페이지 이동 (HTTP/2 오류 방지) → `navigate()` 사용
            page.navigate(url, new Page.NavigateOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED));
            System.out.println("✅ 페이지 이동 완료: " + url);

            // 제목 가져오기
            String title = page.textContent("h1.prod-buy-header__title").trim();
            System.out.println("🔍 크롤링된 제목: " + title);

            // 이미지 가져오기
            String imageUrl = page.getAttribute("img.prod-image__detail", "src");
            if (imageUrl != null && imageUrl.startsWith("//")) {
                imageUrl = "https:" + imageUrl;
            }
            System.out.println("🔍 크롤링된 이미지: " + imageUrl);

            // 데이터 저장
            productData.put("title", title);
            productData.put("image", imageUrl);

            browser.close();
        } catch (Exception e) {
            System.out.println("❌ 크롤링 실패: " + e.getMessage());
            productData.put("error", e.getMessage());
        }

        return productData;
    }

}
