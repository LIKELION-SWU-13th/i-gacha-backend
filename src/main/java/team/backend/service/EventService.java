package team.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import team.backend.dto.EventDto.EventDto;
import team.backend.dto.EventDto.EventGetDto;
import team.backend.domain.Event;
import team.backend.repository.UserRepository;
import team.backend.repository.EventRepository;

import team.backend.apiPayload.exception.EventNotFoundException;
import team.backend.domain.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Autowired
    public EventService(UserRepository userRepository, EventRepository eventRepository) {
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
    }

    public EventGetDto createEvent(Long userId, EventDto eventCreateDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID: " + userId));
        Event event = new Event(user, eventCreateDto.getName());
        Event savedEvent = eventRepository.save(event);
        return new EventGetDto(savedEvent.getId(), savedEvent.getName());
    }

    public EventDto updateEvent(Long eventId, EventDto eventCreateDto) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found with id: " + eventId));

        event.setName(eventCreateDto.getName());

        Event updatedEvent = eventRepository.save(event);

        EventDto eventDto = new EventDto();
        eventDto.setName(updatedEvent.getName());
        return eventDto;
    }

    public void deleteEvent(Long userId, Long eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID: " + userId));
        Event event = eventRepository.findByUserAndId(user, eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not found"));
        eventRepository.delete(event);
    }


    public List<EventGetDto> getAllEvents(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID: " + userId));

        return eventRepository.findAllByUser(user).stream()
                .map(event -> new EventGetDto(
                        event.getId(), event.getName()  // event_id와 name을 포함하여 반환
                ))
                .collect(Collectors.toList());
    }

}
