package team.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team.backend.dto.EventDto.EventDto;
import team.backend.dto.EventDto.EventGetDto;
import team.backend.service.EventService;

import java.util.List;

import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "https://${frontend.domain}") // CORS 적용
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    // 특정 유저의 모든 이벤트 조회
    @GetMapping("/{user_id}/event")
    public ResponseEntity<List<EventGetDto>> getAllEvents(@PathVariable Long user_id) {
        List<EventGetDto> events = eventService.getAllEvents(user_id);
        return ResponseEntity.ok(events);
    }

    // 이벤트 생성
    @PostMapping("/{user_id}/event/create")
    public ResponseEntity<Map<String, Long>> createEvent(@PathVariable Long user_id, @RequestBody EventDto eventCreateDto) {

        Long createdEventId = eventService.createEvent(user_id, eventCreateDto);

        Map<String, Long> response = new HashMap<>();
        response.put("event_id", createdEventId);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 이벤트 삭제
    @DeleteMapping("/{user_id}/event/{event_id}/delete")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long user_id, @PathVariable Long event_id) {
        eventService.deleteEvent(user_id, event_id);
        return ResponseEntity.noContent().build();
    }

    // 이벤트 수정
    @PatchMapping("/{user_id}/event/{event_id}/update")
    public ResponseEntity<EventDto> updateEvent(
            @PathVariable Long user_id,
            @PathVariable Long event_id,
            @RequestBody EventDto eventDto) {

        EventDto updatedEvent = eventService.updateEvent(user_id, event_id, eventDto);
        return ResponseEntity.ok(updatedEvent);
    }
}

