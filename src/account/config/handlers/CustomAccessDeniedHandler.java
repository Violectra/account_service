package account.config.handlers;


import account.models.entities.enums.Action;
import account.services.EventService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.FORBIDDEN;

public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private ObjectMapper objectMapper = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .build();

    private EventService eventService;


    public CustomAccessDeniedHandler(EventService eventService) {
        this.eventService = eventService;
    }

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException
    ) throws IOException, ServletException {
        String email = request.getRemoteUser();

        Map<String, Object> body = new LinkedHashMap<>();
        HttpStatus status = FORBIDDEN;
        body.put("status", status.value());
        body.put("timestamp", new Date());
        body.put("error", status.getReasonPhrase());
        body.put("message", "Access Denied!");
        body.put("path", request.getRequestURI());

        response.setStatus(status.value());
        response.getOutputStream().println(objectMapper.writeValueAsString(body));
        eventService.createEvent(Action.ACCESS_DENIED, email, request.getRequestURI(), request.getRequestURI());
    }
}
