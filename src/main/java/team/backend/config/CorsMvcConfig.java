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
                .exposedHeaders( "*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowCredentials(true)  // 자격 증명(Cookies 등)을 허용
                .allowedHeaders("*")  // 모든 헤더 허용
                .allowedOriginPatterns("*");
//                .allowedOrigins("http://" + frontendDomain)
    }
}
