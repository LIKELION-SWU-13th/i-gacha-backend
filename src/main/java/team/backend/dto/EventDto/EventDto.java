package team.backend.dto.EventDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventDto {
    private Long eventId;
    private String name;

    public EventDto(Long eventId, String name) {
        this.eventId = eventId;
        this.name = name;
    }

}


