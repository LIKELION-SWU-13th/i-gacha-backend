package team.backend.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team.backend.apiPayload.code.EventDto;
import team.backend.service.EventService;

import java.util.List;

@RestController
@RequestMapping("/api/{user_id}/event")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    // 이벤트 전체 조회
    @GetMapping
    public ResponseEntity<List<EventDto>> getAllEvents(@PathVariable Long user_id) {
        List<EventDto> events = eventService.getAllEvents(user_id);
        return ResponseEntity.ok(events);
    }

    // 이벤트 생성
    @PostMapping("/create")
    public ResponseEntity<EventDto> createEvent(@PathVariable Long user_id, @RequestBody EventDto eventDto) {
        EventDto createdEvent = eventService.createEvent(user_id, eventDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEvent);
    }

    // 이벤트 삭제
    @DeleteMapping("/{event_id}/delete")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long user_id, @PathVariable Long event_id) {
        eventService.deleteEvent(user_id, event_id);
        return ResponseEntity.noContent().build();
    }

    // 이벤트 수정
    @PatchMapping("/{event_id}/update")
    public ResponseEntity<EventDto> updateEvent(
            @PathVariable Long user_id,
            @PathVariable Long event_id,
            @RequestBody EventDto eventDto) {

        EventDto updatedEvent = eventService.updateEvent(user_id, event_id, eventDto);
        return ResponseEntity.ok(updatedEvent);
    }
}

