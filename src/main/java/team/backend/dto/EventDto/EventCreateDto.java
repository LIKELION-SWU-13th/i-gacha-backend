package team.backend.dto.EventDto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class EventCreateDto {

    @JsonProperty("event_id")
    private final Long eventId;

    public EventCreateDto(Long eventId) {
        this.eventId = eventId;
    }
}

