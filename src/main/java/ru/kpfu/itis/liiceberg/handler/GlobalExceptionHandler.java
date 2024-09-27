package ru.kpfu.itis.liiceberg.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import ru.kpfu.itis.liiceberg.dto.ErrorDto;
import ru.kpfu.itis.liiceberg.exception.ApiNotAvailableException;
import ru.kpfu.itis.liiceberg.exception.BadArgumentsException;
import ru.kpfu.itis.liiceberg.exception.ConflictException;
import ru.kpfu.itis.liiceberg.exception.NotFoundException;

import javax.security.auth.message.AuthException;


@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorDto> unauthorized() {
        ErrorDto error = new ErrorDto(HttpStatus.UNAUTHORIZED, "Unauthorized");
        return new ResponseEntity<>(error, error.getStatus());
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorDto> userNotFound() {
        ErrorDto error = new ErrorDto(HttpStatus.NOT_FOUND, "User not found");
        return new ResponseEntity<>(error, error.getStatus());
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ErrorDto> auth(AuthException ex) {
        ErrorDto error = new ErrorDto(HttpStatus.UNAUTHORIZED, ex.getMessage());
        return new ResponseEntity<>(error, error.getStatus());
    }

    @ExceptionHandler(BadArgumentsException.class)
    public ResponseEntity<ErrorDto> badArguments(BadArgumentsException ex) {
        ErrorDto error = new ErrorDto(HttpStatus.BAD_REQUEST, ex.getMessage());
        return new ResponseEntity<>(error, error.getStatus());
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorDto> conflict(ConflictException ex) {
        ErrorDto error = new ErrorDto(HttpStatus.CONFLICT, ex.getMessage());
        return new ResponseEntity<>(error, error.getStatus());
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorDto> pageNotFound() {
        ErrorDto error = new ErrorDto(HttpStatus.NOT_FOUND, "Page not found");
        return new ResponseEntity<>(error, error.getStatus());
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorDto> notFound(NotFoundException ex) {
        ErrorDto error = new ErrorDto(HttpStatus.NOT_FOUND, ex.getMessage());
        return new ResponseEntity<>(error, error.getStatus());
    }

    @ExceptionHandler(ApiNotAvailableException.class)
    public ResponseEntity<ErrorDto> apiNotAvailable() {
        ErrorDto error = new ErrorDto(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to get data from the api");
        return new ResponseEntity<>(error, error.getStatus());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorDto> methodNotSupported(HttpRequestMethodNotSupportedException ex) {
        ErrorDto error = new ErrorDto(HttpStatus.METHOD_NOT_ALLOWED, ex.getMessage());
        return new ResponseEntity<>(error, error.getStatus());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorDto> parameterMissing(MissingServletRequestParameterException ex) {
        ErrorDto error = new ErrorDto(HttpStatus.BAD_REQUEST, "Parameter missing: " + ex.getParameterName());
        return new ResponseEntity<>(error, error.getStatus());
    }

}
