package team.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import team.backend.jwt.JWTUtil;
import team.backend.repository.UserRepository;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JWTUtil jwtUtil;


    public Long getUserId(String token) throws AccessDeniedException {

        // 2. 토큰이 없으면 에러 반환
        if (token == null) {

            System.out.println("UserService token == nul: ");
            throw new AccessDeniedException("Unauthorized: No token found");
        }

        // 3. JWT 검증
        if (!jwtUtil.isExpired(token)) {
            // 4. JWT 토큰이 유효한 경우, userId를 반환
            return userRepository.findByEmail(jwtUtil.getUsername(token)).getId();
        } else {

            System.out.println("Unauthorized: Invalid token");
            throw new AccessDeniedException("Unauthorized: Invalid token");
        }
    }

}