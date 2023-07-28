package account.config.handlers;


import account.models.exceptions.BadRequestException;
import account.models.exceptions.NotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@ControllerAdvice
public class ExceptionHandler extends ResponseEntityExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler(BadRequestException.class)
    public final ResponseEntity<Object> handleBadRequestException(BadRequestException ex, WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", BAD_REQUEST.value());
        body.put("timestamp", LocalDateTime.now());
        body.put("error", BAD_REQUEST.getReasonPhrase());
        body.put("message", ex.getMessage());
        body.put("path", ((ServletWebRequest) request).getRequest().getRequestURI());
        return new ResponseEntity<>(body, BAD_REQUEST);
    }
    @org.springframework.web.bind.annotation.ExceptionHandler(NotFoundException.class)
    public final ResponseEntity<Object> handleNotFoundException(NotFoundException ex, WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", NOT_FOUND.value());
        body.put("timestamp", LocalDateTime.now());
        body.put("error", NOT_FOUND.getReasonPhrase());
        body.put("message", ex.getMessage());
        body.put("path", ((ServletWebRequest) request).getRequest().getRequestURI());
        return new ResponseEntity<>(body, NOT_FOUND);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(ConstraintViolationException.class)
    public final ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException ex, WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", BAD_REQUEST.value());
        body.put("timestamp", LocalDateTime.now());
        body.put("error", BAD_REQUEST.getReasonPhrase());
        body.put("message", ex.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage).collect(Collectors.joining(", ")));
        body.put("path", ((ServletWebRequest) request).getRequest().getRequestURI());
        return new ResponseEntity<>(body, BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleErrorResponseException(ErrorResponseException ex, HttpHeaders headers,
                                                                  HttpStatusCode status, WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", status.value());
        body.put("timestamp", LocalDateTime.now());
        body.put("error", "Bad Request");
        body.put("message", ex.getBody().getDetail());
        body.put("path", request.getContextPath());
        return new ResponseEntity<>(body, headers, status);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", status.value());
        body.put("timestamp", LocalDateTime.now());
        body.put("error", HttpStatus.valueOf(status.value()).getReasonPhrase());
        body.put("message", ex.getBody().getDetail());
        body.put("path", ((ServletWebRequest) request).getRequest().getRequestURI());
        return new ResponseEntity<>(body, headers, status);
    }
}
