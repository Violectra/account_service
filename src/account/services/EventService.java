package account.services;

import account.models.dto.EventDTO;
import account.models.entities.enums.Action;
import account.models.entities.EventEntity;
import account.models.entities.enums.Role;
import account.models.entities.UserEntity;
import account.repositories.EventRepository;
import account.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class EventService {

    private static final int ATTEMPTS = 5;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    public EventService(UserRepository userRepository, EventRepository eventRepository) {
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
    }

    @Transactional
    public void createEvent(Action action, String subject, String object, String path) {
        EventEntity event = new EventEntity(new Date(), action, subject, object, path);
        eventRepository.save(event);
        if (event.getAction() == Action.LOGIN_FAILED) {
            userRepository.incrementAttempts(subject);
        }
    }

    @Transactional
    public void checkBruteForce(String subject, String path) {
        UserEntity entity = userRepository.findByEmail(subject);
        if (entity.getAttempts() >= ATTEMPTS) {
            createEvent(Action.BRUTE_FORCE, subject, path, path);
            if (!entity.isAdmin()) {
                // todo: proper lock
                entity.setLocked(true);
                entity.setAttempts(0);
                userRepository.save(entity);
                createEvent(Action.LOCK_USER, subject, "Lock user " + subject, path);
            }
        }
    }

    public List<EventDTO> getAll() {
        List<EventDTO> res = new ArrayList<>();
        eventRepository.findAll().forEach(e -> res.add(new EventDTO(e.getDate(), e.getAction(), e.getSubject(),
                e.getObject(), e.getPath())));
        return res;
    }
}
