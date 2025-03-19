package team.backend.config;

import com.microsoft.playwright.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

@Configuration
public class PlaywrightConfig {

    @Bean
    public Playwright playwright() {
        return Playwright.create();
    }

    @Bean
    public Browser browser(Playwright playwright) {
        return playwright.chromium().launch(new BrowserType.LaunchOptions()
                .setHeadless(true) // 서버에서는 true로 설정
                .setArgs(List.of(
                        "--disable-blink-features=AutomationControlled",
                        "--no-sandbox",
                        "--disable-gpu"
                ))
        );
    }

    @Bean
    public BrowserContext browserContext(Browser browser) {
        return browser.newContext(new Browser.NewContextOptions()
                .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.5735.199 Safari/537.36")
                .setExtraHTTPHeaders(Map.of(
                        "accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8",
                        "accept-encoding", "gzip, deflate",
                        "accept-language", "en-US,en;q=0.5"
                ))
        );
    }
}
