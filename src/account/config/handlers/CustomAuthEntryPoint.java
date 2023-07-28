package account.config.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

public class CustomAuthEntryPoint implements AuthenticationEntryPoint {
    private ObjectMapper objectMapper = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .build();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        Map<String, Object> body = new LinkedHashMap<>();
        HttpStatus status = UNAUTHORIZED;
        String uri = request.getRequestURI();
        body.put("status", status.value());
        body.put("timestamp", new Date());
        body.put("error", status.getReasonPhrase());
        String message = authException.getMessage();
        if (authException instanceof AccountStatusException) {
            message = "User account is locked";
        }
        if (authException instanceof LockedException) {
            message = "User account is locked";
        }
        body.put("message", message);
        body.put("path", uri);

        response.setStatus(status.value());
        response.getOutputStream().println(objectMapper.writeValueAsString(body));
    }
}
