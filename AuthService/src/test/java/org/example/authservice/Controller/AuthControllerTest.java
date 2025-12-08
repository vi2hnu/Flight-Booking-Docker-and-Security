package org.example.authservice.Controller;


import org.example.authservice.controllers.AuthController;
import org.example.authservice.dto.LoginDTO;
import org.example.authservice.dto.MessageResponse;
import org.example.authservice.dto.SignupDTO;
import org.example.authservice.dto.UserInfoResponse;
import org.example.authservice.model.entity.Role;
import org.example.authservice.model.entity.Users;
import org.example.authservice.model.enums.eRole;
import org.example.authservice.repository.RoleRepository;
import org.example.authservice.repository.UsersRepository;
import org.example.authservice.service.jwt.JwtUtils;
import org.example.authservice.service.user.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UsersRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private AuthController authController;

    private UserDetailsImpl userDetails;
    private ResponseCookie jwtCookie;

    @BeforeEach
    void setup() {
        userDetails = new UserDetailsImpl(
                1L,
                "vishnu",
                "vishnu@mail.com",
                "pass",
                List.of(() -> "ROLE_USER")
        );
        jwtCookie = ResponseCookie.from("jwt", "token").build();
    }

    @Test
    void signin_success() {
        LoginDTO loginDTO = new LoginDTO("vishnu", "pass");

        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(userDetails);
        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(jwtUtils.generateJwtCookie(userDetails)).thenReturn(jwtCookie);

        ResponseEntity<?> response = authController.authenticateUser(loginDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertEquals(jwtCookie.toString(), response.getHeaders().getFirst("Set-Cookie"));

        UserInfoResponse body = (UserInfoResponse) response.getBody();
        assertNotNull(body);
        assertEquals("vishnu", body.getUsername());
        assertEquals("vishnu@mail.com", body.getEmail());
        assertTrue(body.getRoles().contains("ROLE_USER"));

        verify(authenticationManager).authenticate(any());
        verify(jwtUtils).generateJwtCookie(userDetails);
    }

    @Test
    void signin_invalidCredentials_throws() {
        LoginDTO loginDTO = new LoginDTO("vishnu", "wrongpass");
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Invalid"));

        assertThrows(BadCredentialsException.class,
                () -> authController.authenticateUser(loginDTO));
    }

    @Test
    void signup_success_withDefaultRole() {
        SignupDTO signupDTO = new SignupDTO("newuser", "new@mail.com", null, "pass");

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("new@mail.com")).thenReturn(false);

        Role userRole = new Role();
        userRole.setName(eRole.USER);
        when(roleRepository.findByName(eRole.USER)).thenReturn(Optional.of(userRole));
        when(encoder.encode("pass")).thenReturn("encoded");

        ResponseEntity<?> response = authController.registerUser(signupDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        MessageResponse body = (MessageResponse) response.getBody();
        assertNotNull(body);
        assertEquals("User registered successfully!", body.getMessage());

        ArgumentCaptor<Users> captor = ArgumentCaptor.forClass(Users.class);
        verify(userRepository).save(captor.capture());
        Users savedUser = captor.getValue();
        assertEquals("newuser", savedUser.getUsername());
        assertEquals("new@mail.com", savedUser.getEmail());
        assertEquals("encoded", savedUser.getPassword());
        assertTrue(savedUser.getRoles().contains(userRole));
    }

    @Test
    void signup_success_withAdminRole() {
        Set<String> roles = new HashSet<>();
        roles.add("admin");
        SignupDTO signupDTO = new SignupDTO("adminuser", "admin@mail.com", roles, "pass");

        when(userRepository.existsByUsername("adminuser")).thenReturn(false);
        when(userRepository.existsByEmail("admin@mail.com")).thenReturn(false);

        Role adminRole = new Role();
        adminRole.setName(eRole.ADMIN);
        when(roleRepository.findByName(eRole.ADMIN)).thenReturn(Optional.of(adminRole));
        when(encoder.encode("pass")).thenReturn("encoded");

        ResponseEntity<?> response = authController.registerUser(signupDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        ArgumentCaptor<Users> captor = ArgumentCaptor.forClass(Users.class);
        verify(userRepository).save(captor.capture());
        Users savedUser = captor.getValue();
        assertEquals("adminuser", savedUser.getUsername());
        assertTrue(savedUser.getRoles().contains(adminRole));
    }

    @Test
    void signup_success_withStaffRole() {
        Set<String> roles = new HashSet<>();
        roles.add("staff");
        SignupDTO signupDTO = new SignupDTO("staffuser", "staff@mail.com", roles, "pass");

        when(userRepository.existsByUsername("staffuser")).thenReturn(false);
        when(userRepository.existsByEmail("staff@mail.com")).thenReturn(false);

        Role staffRole = new Role();
        staffRole.setName(eRole.STAFF);
        when(roleRepository.findByName(eRole.STAFF)).thenReturn(Optional.of(staffRole));
        when(encoder.encode("pass")).thenReturn("encoded");

        ResponseEntity<?> response = authController.registerUser(signupDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        ArgumentCaptor<Users> captor = ArgumentCaptor.forClass(Users.class);
        verify(userRepository).save(captor.capture());
        Users savedUser = captor.getValue();
        assertTrue(savedUser.getRoles().contains(staffRole));
    }

    @Test
    void signup_existingUsername_returnsBadRequest() {
        SignupDTO signupDTO = new SignupDTO("vishnu", "vishnu@mail.com", null, "pass");
        when(userRepository.existsByUsername("vishnu")).thenReturn(true);

        ResponseEntity<?> response = authController.registerUser(signupDTO);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        MessageResponse body = (MessageResponse) response.getBody();
        assertNotNull(body);
        assertEquals("Error: Username is already taken!", body.getMessage());

        verify(userRepository, never()).save(any());
    }

    @Test
    void signup_existingEmail_returnsBadRequest() {
        SignupDTO signupDTO = new SignupDTO("user", "vishnu@mail.com", null, "pass");
        when(userRepository.existsByUsername("user")).thenReturn(false);
        when(userRepository.existsByEmail("vishnu@mail.com")).thenReturn(true);

        ResponseEntity<?> response = authController.registerUser(signupDTO);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        MessageResponse body = (MessageResponse) response.getBody();
        assertNotNull(body);
        assertEquals("Error: Email is already in use!", body.getMessage());

        verify(userRepository, never()).save(any());
    }

    @Test
    void signup_roleNotFound_throwsException() {
        SignupDTO signupDTO = new SignupDTO("newuser", "new@mail.com", null, "pass");

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("new@mail.com")).thenReturn(false);
        when(roleRepository.findByName(eRole.USER)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> authController.registerUser(signupDTO));
    }

    @Test
    void signout_success() {
        when(jwtUtils.getCleanJwtCookie()).thenReturn(jwtCookie);

        ResponseEntity<?> response = authController.logoutUser();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(jwtCookie.toString(), response.getHeaders().getFirst("Set-Cookie"));
        MessageResponse body = (MessageResponse) response.getBody();
        assertNotNull(body);
        assertEquals("You've been signed out!", body.getMessage());

        verify(jwtUtils).getCleanJwtCookie();
    }
}