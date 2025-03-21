package team.backend.dto.EventDto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventCreateDto {
    @JsonProperty("event_id")
    private Long eventId;

    public EventCreateDto(Long eventId) {
        this.eventId = eventId;
    }
}
