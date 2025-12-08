package org.example.authservice.configuration;

import org.example.authservice.service.jwt.AuthEntryPointJwt;
import org.example.authservice.service.jwt.AuthTokenFilter;
import org.example.authservice.service.jwt.JwtUtils;
import org.example.authservice.service.user.UserDetailsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

class FilterTest {

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private AuthEntryPointJwt unauthorizedHandler;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private Filter filter;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void authenticationJwtTokenFilter_isNotNull() {
        AuthTokenFilter tokenFilter = filter.authenticationJwtTokenFilter();
        assertNotNull(tokenFilter);
    }

    @Test
    void authenticationProvider_isNotNull() {
        DaoAuthenticationProvider provider = filter.authenticationProvider();
        assertNotNull(provider);
    }

    @Test
    void passwordEncoder_isNotNull() {
        PasswordEncoder encoder = filter.passwordEncoder();
        assertNotNull(encoder);
    }
}
