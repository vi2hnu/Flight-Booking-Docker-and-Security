package org.example.authservice.dto;

public record ChangePasswordDTO (
        String username,
        String oldPassword,
        String newPassword
){}
