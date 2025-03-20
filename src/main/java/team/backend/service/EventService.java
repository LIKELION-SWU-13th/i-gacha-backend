package team.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import team.backend.dto.EventDto;
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

    public EventDto createEvent(Long userId, EventDto eventDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID: " + userId));
        Event event = new Event(user, eventDto.getName());
        Event savedEvent = eventRepository.save(event);
        return new EventDto(savedEvent.getId(), savedEvent.getName());
    }

    public void deleteEvent(Long userId, Long eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID: " + userId));
        Event event = eventRepository.findByUserAndId(user, eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not found"));
        eventRepository.delete(event);
    }

    public EventDto updateEvent(Long userId, Long eventId, EventDto eventDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID: " + userId));
        Event event = eventRepository.findByUserAndId(user, eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not found"));
        event.setName(eventDto.getName());
        Event updatedEvent = eventRepository.save(event);
        return new EventDto(updatedEvent.getId(),updatedEvent.getName());
    }

    public List<EventDto> getAllEvents(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID: " + userId));

        return eventRepository.findAllByUser(user).stream()
                .map(event -> new EventDto(
                        event.getId(),event.getName()
                ))
                .collect(Collectors.toList());
    }

}
