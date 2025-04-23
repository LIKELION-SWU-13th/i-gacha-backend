package team.backend.dto.EventDto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventGetDto {
    @JsonProperty("event_id")
    private Long eventId;
    private String name;

    public EventGetDto(Long eventId, String name) {
        this.eventId = eventId;
        this.name = name;
    }

}