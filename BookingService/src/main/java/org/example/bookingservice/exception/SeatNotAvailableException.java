package org.example.bookingservice.exception;

public class SeatNotAvailableException extends RuntimeException {
    public SeatNotAvailableException(String message) {
        super(message);
    }
}
