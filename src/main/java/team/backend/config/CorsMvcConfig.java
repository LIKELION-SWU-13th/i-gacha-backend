package team.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsMvcConfig implements WebMvcConfigurer {

    @Value("${frontend.domain}")  // application.yml에서 정의한 URL 값 읽기
    private String frontendDomain;

    @Override
    public void addCorsMappings(CorsRegistry corsRegistry) {
        corsRegistry.addMapping("/**")
                .allowedOrigins("http://localhost:3000", "http://localhost:3001", "http://localhost:3007", "https://" + frontendDomain)  // 프론트엔드 허용
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")  // 허용할 HTTP 메서드
                .allowCredentials(true)  // 인증 포함 허용
                .allowedHeaders("*")  // 모든 헤더 허용
                .exposedHeaders("Authorization", "Content-Type")  // 클라이언트가 읽을 수 있는 헤더
                .maxAge(3600);  // Preflight 요청 캐시 시간 (초)
    }
}
