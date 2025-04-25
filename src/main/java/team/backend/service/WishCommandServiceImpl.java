package team.backend.service;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import io.github.bonigarcia.wdm.WebDriverManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import team.backend.apiPayload.code.status.ErrorStatus;
import team.backend.apiPayload.exception.handler.EventHandler;
import team.backend.domain.Event;
import team.backend.domain.Wish;
import team.backend.dto.WishDTO.WishRequestDTO;
import team.backend.repository.EventRepository;
import team.backend.repository.WishRepository;

import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class WishCommandServiceImpl implements WishCommandService {

    private final WishRepository wishRepository;
    private final EventRepository eventRepository;

    // 웹 드라이버 주소
    @Value("${webdriver.chrome.path}")
    private String chromeDriverPath;

    // 크롤링 서버 주소
    @Value("${crawling.server.url}")
    private String crawlingServerUrl;

    // 위시 생성
    @Override
    @Transactional
    public Wish joinEvent(Long userId, Long eventId, WishRequestDTO.CreateRqDTO request){
        validateUrlFormat(request.getLink());
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
        validateUrlFormat(request.getLink());
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

    private final RestTemplate restTemplate = new RestTemplate();

    //크롤링 코드
    @Override
    public Map<String, String> fetchWishData(String url) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("url", url);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    crawlingServerUrl + "/crawl",
                    entity,
                    Map.class
            );

            Map<String, String> result = response.getBody();

            if (result == null
                    || result.get("title") == null
                    || result.get("image") == null ){
                return fallbackWithPlaywright(url);
                //throw new EventHandler(ErrorStatus._CRAWLING_ERROR);
            }

            return result;

        } catch (Exception e) {
            //throw new EventHandler(ErrorStatus._CRAWLING_ERROR);
            return fallbackWithPlaywright(url);
        }
    }

    @Override
    public void validateUrlFormat(String url) {
        if (!url.matches("^https://www\\.coupang\\.com/vp/products/\\d+.*$")) {
            throw new EventHandler(ErrorStatus._INVALID_URL_FORMAT);
        }
    }

    public Map<String, String> fallbackWithPlaywright(String url) {
        Map<String, String> result = new HashMap<>();

        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
            Page page = browser.newPage();
            page.navigate(url, new Page.NavigateOptions().setTimeout(60000));

            // og:title에서 상품명 추출
            String title = page.locator("meta[property='og:title']").getAttribute("content");

            if (title == null) {
                title = page.title(); // 대체용
            }

            result.put("title", title);
            result.put("image", ""); // 이미지 없이 빈 값 세팅

        } catch (Exception e) {
            System.err.println("Playwright fallback error: " + e.getMessage());
            result.put("title", null); // 최악의 경우 null 반환
            result.put("image", "");
        }

        return result;
    }

}