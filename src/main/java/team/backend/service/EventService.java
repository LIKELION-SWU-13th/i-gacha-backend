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

    public EventDto updateEvent(Long userId, Long eventId, EventDto eventDto) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found with id: " + eventId));

        // 해당 이벤트가 userId의 소유자인지 검증
        if (!event.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("User does not have permission to update this event");
        }

        event.setName(eventDto.getName());
        eventRepository.save(event);

        return new EventDto(event.getName());

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
                        event.getId(), event.getName()
                ))
                .collect(Collectors.toList());
    }

}
