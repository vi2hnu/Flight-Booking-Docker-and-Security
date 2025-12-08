package org.example.bookingservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException e) {
        Map<String, String> errorMap = new HashMap<>();

        List<ObjectError> errorList = e.getBindingResult().getAllErrors();

        errorList.forEach(error -> {
            if (error instanceof FieldError fieldError) {
                String fieldName = fieldError.getField();
                String message = fieldError.getDefaultMessage();
                errorMap.put(fieldName, message);
            }
            else{
                String message = error.getDefaultMessage();
                errorMap.put("globalError", message);
            }
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMap);
    }


    @ExceptionHandler(UsersNotFoundException.class)
    public ResponseEntity<String> handleUserNotFound(UsersNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(TicketNotFoundException.class)
    public ResponseEntity<String> handleTicketNotFound(TicketNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidScheduleTimeException.class)
    public ResponseEntity<String> handleInvalidScheduleTime(InvalidScheduleTimeException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SeatNotAvailableException.class)
    public ResponseEntity<String> handleSeatNotAvailable(SeatNotAvailableException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    // fallback for any other exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
