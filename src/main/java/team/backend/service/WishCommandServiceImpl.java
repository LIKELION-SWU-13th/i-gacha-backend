package team.backend.service;

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
import org.springframework.stereotype.Service;
import team.backend.apiPayload.code.status.ErrorStatus;
import team.backend.apiPayload.exception.handler.EventHandler;
import team.backend.domain.Event;
import team.backend.domain.Wish;
import team.backend.dto.WishDTO.WishRequestDTO;
import team.backend.repository.EventRepository;
import team.backend.repository.WishRepository;

import java.time.Duration;
import java.util.HashMap;
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

    // 크롤링 코드
    // 크롤링 코드
    @Transactional
    public Map<String, String> fetchWishData(String url){
        Map<String, String> productData = new HashMap<>();

        try {
            WebDriverManager.chromedriver().setup();

            // 👉 여기 수정
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--disable-gpu");

            WebDriver driver = new ChromeDriver(options);

            driver.get(url);
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            WebElement titleElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("h1.prod-buy-header__title")));
            String title = titleElement.getText();
            productData.put("title", title);

            WebElement imageElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("img.prod-image__detail")));
            String imageUrl = imageElement.getAttribute("src");

            String highResImageUrl = imageElement.getAttribute("data-zoom-image-url");
            if (highResImageUrl != null && !highResImageUrl.isEmpty()) {
                imageUrl = highResImageUrl;
            }
            if (imageUrl.startsWith("//")) {
                imageUrl = "https:" + imageUrl;
            }

            productData.put("image", imageUrl);

            driver.quit();
        } catch (Exception e) {
            productData.put("error", e.getMessage());
        }

        return productData;
    }

}