package team.backend.service;

import org.springframework.stereotype.Service;
import team.backend.dto.EventDto;
import team.backend.domain.Event;
import team.backend.repository.EventRepository;
import team.backend.apiPayload.exception.EventNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventService {

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public List<EventDto> getAllEvents(Long userId) {
        List<Event> events = eventRepository.findByUserId(userId);
        return events.stream()
                .map(event -> new EventDto(event.getId(), event.getName(), event.getStartDate(), event.getEndDate()))
                .collect(Collectors.toList());
    }

    public EventDto createEvent(Long userId, EventDto eventDto) {
        Event event = new Event(userId, eventDto.getName(), eventDto.getStartDate(), eventDto.getEndDate());
        Event savedEvent = eventRepository.save(event);
        return new EventDto(savedEvent.getId(), savedEvent.getName(), savedEvent.getStartDate(), savedEvent.getEndDate());
    }

    public void deleteEvent(Long userId, Long eventId) {
        Event event = eventRepository.findByUserIdAndId(userId, eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not found"));
        eventRepository.delete(event);
    }

    public EventDto updateEvent(Long userId, Long eventId, EventDto eventDto) {
        Event event = eventRepository.findByUserIdAndId(userId, eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not found"));
        event.setName(eventDto.getName());
        event.setStartDate(eventDto.getStartDate());
        event.setEndDate(eventDto.getEndDate());
        Event updatedEvent = eventRepository.save(event);
        return new EventDto(updatedEvent.getId(), updatedEvent.getName(), updatedEvent.getStartDate(), updatedEvent.getEndDate());
    }
}
