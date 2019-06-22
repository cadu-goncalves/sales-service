package com.viniland.sales.control;

import com.viniland.sales.domain.rest.ResourceError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

/**
 * Handles overall exceptions
 */
@ControllerAdvice
@RestController
public class ErrorController {

    /**
     * Custom error output when {@link MethodArgumentNotValidException} occurs.
     *
     * @param exception
     * @return {@link ResponseEntity} with errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResourceError> handleValidationErrors(MethodArgumentNotValidException exception) {
        ResourceError error = new ResourceError();

        // Collect validation messages
       exception.getBindingResult()
                .getAllErrors().stream()
                .map(ObjectError::getDefaultMessage)
                .forEach(error::addMessage);

        error.setStatus(HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(error, error.getStatus());
    }

}
