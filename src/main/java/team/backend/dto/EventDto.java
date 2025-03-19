package team.backend.dto;

import lombok.Getter;
import lombok.Setter;

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

