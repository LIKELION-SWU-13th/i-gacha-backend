package team.backend.dto;

import lombok.Getter;
import lombok.Setter;
import team.backend.domain.User;  // User 타입을 사용하기 위해 import

@Getter
@Setter
public class EventDto {
    private Long userId;
    private String name;

    public EventDto(Long userId, String name) {
        this.userId = userId;
        this.name = name;
    }

}

