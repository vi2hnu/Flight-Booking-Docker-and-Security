package org.example.bookingservice.exception;

public class InvalidScheduleTimeException extends RuntimeException {
    public InvalidScheduleTimeException(String message) {
        super(message);
    }
}
