package org.example.authservice.controllers;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.authservice.dto.*;
import org.example.authservice.model.entity.Users;
import org.example.authservice.service.auth.AuthService;
import org.example.authservice.service.jwt.JwtUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtUtils jwtUtils;

    public AuthController(AuthService authService,JwtUtils jwtUtils){
        this.authService = authService;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/signIn")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginDTO loginRequest) {
        AuthResult result = authService.login(loginRequest);
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, result.jwtCookie().toString())
                .body(result.userInfo());
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupDTO signUpRequest) {
        if(!authService.signUp(signUpRequest)){
            return ResponseEntity.badRequest().body(new MessageResponse("Error: account already exist"));
        }
        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    @PostMapping("/signout")
    public ResponseEntity<?> logoutUser() {
        ResponseCookie cookie = jwtUtils.getCleanJwtCookie();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new MessageResponse("You've been signed out!"));
    }

    @PostMapping("/user")
    public ResponseEntity<Users> getUser(@RequestBody GetUserDTO dto) {
        return ResponseEntity.ok(authService.getUser(dto));
    }

    @PostMapping("/change/password")
    public ResponseEntity<MessageResponse> changePassword(@RequestBody ChangePasswordDTO request){
        authService.changePassword(request);
        return ResponseEntity.ok(new MessageResponse("User password has been changed. Please login again"));
    }

    @GetMapping("/oauth2/success")
    public Map<String,String> oauth2Success(@AuthenticationPrincipal OAuth2User oauth2User) {
        String token = (String) oauth2User.getAttribute("token");
        Map<String,String> response = new HashMap<>();
        response.put("jwt", token);
        response.put("email", oauth2User.getAttribute("email"));
        return response;
    }
}