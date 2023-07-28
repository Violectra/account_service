package account.config.handlers;

import account.models.entities.enums.Action;
import account.services.EventService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationFailureLockedEvent;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationFailureListener implements
        ApplicationListener<AbstractAuthenticationFailureEvent> {

    private EventService eventService;

    private HttpServletRequest request;
    public AuthenticationFailureListener(EventService eventService, HttpServletRequest request) {
        this.eventService = eventService;
        this.request = request;
    }

    @Override
    public void onApplicationEvent(AbstractAuthenticationFailureEvent e) {
        Authentication authentication = e.getAuthentication();
        String subject = "Anonymous";
        if (authentication != null) {
            subject = authentication.getName().toLowerCase();
        }
        String requestURI = request.getRequestURI();
        if (!(e instanceof AuthenticationFailureLockedEvent)) {
            eventService.createEvent(Action.LOGIN_FAILED, subject, requestURI, requestURI);
            eventService.checkBruteForce(subject, requestURI);
        }
    }
}
