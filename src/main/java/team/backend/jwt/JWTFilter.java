package team.backend.jwt;


import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import team.backend.dto.CustomOAuth2User;
import team.backend.dto.UserDTO;

import java.io.IOException;
import java.io.PrintWriter;

public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    public JWTFilter(JWTUtil jwtUtil) {

        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 재로그인 무한루프 방지
        String requestUri = request.getRequestURI();

        System.out.println("requestUri: " + requestUri);
        // Swagger UI 및 OpenAPI 문서 경로는 필터를 건너뛰기
        if (requestUri.startsWith("/swagger-ui") || requestUri.startsWith("/v3/api-docs")) {
            filterChain.doFilter(request, response); // Swagger 관련 경로를 필터링에서 제외
            return;
        }

        if (requestUri.matches("^\\/login(?:\\/.*)?$") || requestUri.matches("^\\/api\\/user\\/tokenVerification(?:\\/.*)?$")) {

            filterChain.doFilter(request, response);
            return;
        }
        if (requestUri.matches("^\\/oauth2(?:\\/.*)?$")) {

            filterChain.doFilter(request, response);
            return;
        }

        // 헤더에서 access키에 담긴 토큰을 꺼냄
        String accessToken = request.getHeader("access");

        // 토큰 만료 여부 확인, 만료시 다음 필터로 넘기지 않음
        try {
            jwtUtil.isExpired(accessToken);
        } catch (ExpiredJwtException e) {
            //response body
            PrintWriter writer = response.getWriter();
            writer.print("access token expired");

//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        } catch (IllegalArgumentException e) {
            PrintWriter writer = response.getWriter();
            writer.print("access token empty");

//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        //토큰 소멸 시간 검증
        if (jwtUtil.isExpired(accessToken)) {

            filterChain.doFilter(request, response);

            //조건이 해당되면 메소드 종료 (필수)
            return;
        }

        //토큰에서 username과 role 획득
        String username = jwtUtil.getUsername(accessToken);
        String role = jwtUtil.getRole(accessToken);

        //userDTO를 생성하여 값 set
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail(username);
        userDTO.setRole(role);

        //UserDetails에 회원 정보 객체 담기
        CustomOAuth2User customOAuth2User = new CustomOAuth2User(userDTO);

        //스프링 시큐리티 인증 토큰 생성
        Authentication authToken = new UsernamePasswordAuthenticationToken(customOAuth2User, null, customOAuth2User.getAuthorities());
        //세션에 사용자 등록
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }
}