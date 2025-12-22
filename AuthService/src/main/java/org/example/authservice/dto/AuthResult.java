package org.example.authservice.dto;

import org.springframework.http.ResponseCookie;

public record AuthResult(
        ResponseCookie jwtCookie,
        UserInfoResponse userInfo
) {
}
