package team.backend.dto;

import lombok.Getter;
import lombok.Setter;
import team.backend.domain.User;  // User 타입을 사용하기 위해 import
import java.time.LocalDateTime;

@Getter
@Setter
public class EventDto {
    private User user;
    private String name;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    // 생성자는 User 객체를 초기화
    public EventDto(User user, String name, LocalDateTime startDate, LocalDateTime endDate) {
        this.user = user;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
    }

}

