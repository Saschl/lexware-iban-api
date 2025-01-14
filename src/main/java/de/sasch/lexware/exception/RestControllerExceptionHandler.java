package de.sasch.lexware.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestControllerExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = InvalidIbanException.class)
    protected ResponseEntity<Object> handleBadIban(
            RuntimeException ex, WebRequest request) {
        return handleExceptionInternal(ex, "Provided IBAN is not valid.",
                new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(value = ApiAccessException.class)
    protected ResponseEntity<Object> handleApiAccessError(
            RuntimeException ex, WebRequest request) {
        return handleExceptionInternal(ex, "Could not call OpenIBAN api.",
                new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }
}
