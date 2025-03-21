package team.backend.dto.EventDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventCreateDto {
    private Long eventId;

    public EventCreateDto(Long eventId) {
        this.eventId = eventId;
    }
}
