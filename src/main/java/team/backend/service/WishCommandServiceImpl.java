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
    private final BrowserContext browserContext;  // Spring Bean으로 관리하는 Playwright Context

    // 위시 생성
    @Override
    @Transactional
    public Wish joinEvent(Long userId, Long eventId, WishRequestDTO.CreateRqDTO request) {
        // 이벤트 생성 여부 확인
        boolean isChecked = eventRepository.isUserCreator(userId, eventId);
        if (!isChecked) {
            throw new EventHandler(ErrorStatus._USER_NOT_CREATE_EVENT);
        }

        Event eventResult = eventRepository.findById(eventId).orElseThrow(EntityNotFoundException::new);

        // 크롤링 수행
        Map<String, String> crawData = fetchWishData(request.getLink());

        // Wish 객체 생성
        Wish newWish = new Wish();
        newWish.setName(crawData.get("title"));
        newWish.setLink(request.getLink());
        newWish.setImageUrl(crawData.get("image"));

        // 이벤트 연결
        newWish.setEvent(eventResult);
        wishRepository.save(newWish);

        return newWish;
    }

    // 위시 수정
    @Override
    @Transactional
    public Wish updateWish(Long userId, Long eventId, Long wishId, WishRequestDTO.CreateRqDTO request) {
        // 이벤트 & 위시 존재 확인
        int isChecked = eventRepository.existsWishForUser(userId, eventId, wishId);
        if (isChecked != 1) {
            throw new EventHandler(ErrorStatus._USER_EVENT_WISH);
        }

        Wish wish = wishRepository.findById(wishId)
                .orElseThrow(() -> new EventHandler(ErrorStatus._WISH_NOT_FOUND));

        // 크롤링 수행
        Map<String, String> crawData = fetchWishData(request.getLink());

        // 정보 업데이트
        wish.update(crawData.get("title"), request.getLink(), crawData.get("image"));
        wishRepository.save(wish);

        return wish;
    }

    // 위시 삭제
    @Override
    @Transactional
    public void delSerWish(Long userId, Long eventId, Long wishId) {
        // 삭제 가능 여부 확인
        int isChecked = eventRepository.existsWishForUser(userId, eventId, wishId);
        if (isChecked != 1) {
            throw new EventHandler(ErrorStatus._USER_EVENT_WISH);
        }

        wishRepository.deleteById(wishId);
    }

    // 위시 조회
    @Override
    @Transactional
    public void checkEvent(Long userId, Long eventId) {
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
                    .setHeadless(true) // 서버에서는 true, 로컬 디버깅 시 false 가능
                    .setArgs(List.of(
                            "--disable-blink-features=AutomationControlled", // 자동화 감지 방지
                            "--no-sandbox",
                            "--disable-gpu"
                    ))
            );

            // 브라우저 컨텍스트 설정 (HTTP/1.1 강제 적용)
            BrowserContext context = browser.newContext(new Browser.NewContextOptions()
                    .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.5735.199 Safari/537.36")
                    .setViewportSize(1920, 1080) // 해상도 설정
                    .setExtraHTTPHeaders(Map.of(
                            "accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8",
                            "accept-encoding", "gzip, deflate", // HTTP/1.1 강제 적용
                            "accept-language", "en-US,en;q=0.5",
                            "upgrade-insecure-requests", "1"
                    ))
            );

            Page page = context.newPage();

            // 자동화 탐지 방지
            page.addInitScript("Object.defineProperty(navigator, 'webdriver', {get: () => undefined})");

            // 페이지 이동 (쿠팡 HTTP/2 차단 방지)
            try {
                page.navigate(url, new Page.NavigateOptions().setWaitUntil(WaitUntilState.NETWORKIDLE));
                System.out.println("✅ 페이지 이동 완료: " + url);
            } catch (Exception e) {
                System.out.println("❌ 페이지 이동 실패: " + e.getMessage());
                productData.put("error", "페이지 로딩 실패");
                return productData;
            }

            // 네트워크 안정성을 위해 대기
            page.waitForTimeout(5000);
            page.evaluate("window.scrollTo(0, document.body.scrollHeight)"); // 페이지 끝까지 스크롤

            // 제목 가져오기
            String title = "";
            try {
                title = page.textContent("h1.prod-buy-header__title").trim();
            } catch (Exception e) {
                System.out.println("❌ 제목 크롤링 실패, 기본값 설정");
                title = "상품 정보 없음";
            }

            // 이미지 가져오기
            String imageUrl = "";
            try {
                imageUrl = page.getAttribute("img.prod-image__detail", "src");
                if (imageUrl != null && imageUrl.startsWith("//")) {
                    imageUrl = "https:" + imageUrl;
                }
            } catch (Exception e) {
                System.out.println("❌ 이미지 크롤링 실패, 기본값 설정");
                imageUrl = "https://via.placeholder.com/300";
            }

            System.out.println("🔍 크롤링된 제목: " + title);
            System.out.println("🔍 크롤링된 이미지: " + imageUrl);

            // 데이터 저장
            productData.put("title", title);
            productData.put("image", imageUrl);

            page.close();
            browser.close();
        } catch (Exception e) {
            System.out.println("❌ 크롤링 실패: " + e.getMessage());
            productData.put("error", e.getMessage());
        }

        return productData;
    }

}
