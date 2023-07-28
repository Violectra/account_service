package account.controllers;

import account.models.dto.EventDTO;
import account.services.EventService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/security")
public class SecurityController {
    private final EventService eventService;

    public SecurityController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping({"/events", "/events/"})
    public List<EventDTO> getEvents() {
        return eventService.getAll();
    }
}
