package org.example.authservice.exception;

public class UsersNotFoundException extends RuntimeException{
    public UsersNotFoundException(String message){
        super(message);
    }
}
