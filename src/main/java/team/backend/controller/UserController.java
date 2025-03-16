package team.backend.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team.backend.service.UserService;


@RestController
@RequestMapping("/api/user")
public class UserController {


    @Autowired
    private UserService userService;
    @Value("${frontend.domain}")  // application.yml에서 정의한 URL 값 읽기
    private String frontendDomain;

    @GetMapping("/tokenVerification")
    public ResponseEntity<String> getProtectedData(HttpServletRequest request, HttpServletResponse response) {

        // 1. 쿠키에서 'Authorization' 쿠키를 가져옴
        Cookie[] cookies = request.getCookies();
        String token = null;

        System.out.println("getProtectedData");

        if (cookies != null) {

            for (Cookie cookie : cookies) {
                if ("Authorization".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        try {
            Long id = userService.getUserId(token);
            deleteCookie(response, "Authorization");

            // 4. JWT 토큰이 유효한 경우, 헤더에 포함하여 id와 함께 응답
            return ResponseEntity.ok()
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .body("user_id=" + id);
        } catch (AccessDeniedException e) {
            System.out.println("AccessDeniedException " + e.getMessage());
            return ResponseEntity.status(401).body(e.getMessage());
        } catch (Exception e) {
            System.out.println("Exception " + e.getMessage());
            // 기타 실패 응답
            return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
        }


    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok().body("okokokokok");
    }

    private void deleteCookie(HttpServletResponse response, String cookieName) {
        Cookie cookie = new Cookie(cookieName, "");
        cookie.setMaxAge(0); // 쿠키 즉시 삭제
        cookie.setPath("/");  // 모든 경로에서 삭제
        cookie.setHttpOnly(true); // HttpOnly 설정
//        cookie.setDomain(frontendDomain);
        response.addCookie(cookie);
    }
}