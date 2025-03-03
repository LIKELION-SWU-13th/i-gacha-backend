package team.backend.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;
import team.backend.dto.WishDto;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;

@Service
public class WishService {

    public WishDto fetchWishData(String url) {
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
            return new WishDto(title, imageUrl);
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
