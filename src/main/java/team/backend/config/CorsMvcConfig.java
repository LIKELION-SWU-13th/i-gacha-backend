package team.backend.config;

import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

public class CorsMvcConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry corsRegistry) {

        corsRegistry.addMapping("/**")
//                .exposedHeaders("Set-Cookie")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowCredentials(true)  // 자격 증명(Cookies 등)을 허용
                .allowedHeaders("*")  // 모든 헤더 허용
                .allowedOrigins("http://localhost:3000");
    }
}
