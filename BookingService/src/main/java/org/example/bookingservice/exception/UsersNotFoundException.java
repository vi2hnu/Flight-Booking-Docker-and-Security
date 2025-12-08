package org.example.bookingservice.exception;

public class UsersNotFoundException extends RuntimeException {
    public UsersNotFoundException(String message) {
        super(message);
    }
}
