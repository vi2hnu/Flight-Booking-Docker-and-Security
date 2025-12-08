package org.example.authservice.service;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.authservice.service.jwt.AuthEntryPointJwt;
import org.example.authservice.service.jwt.AuthTokenFilter;
import org.example.authservice.service.jwt.JwtUtils;
import org.example.authservice.service.user.UserDetailsImpl;
import org.example.authservice.service.user.UserDetailsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private AuthEntryPointJwt authEntryPointJwt;

    private JwtUtils jwtUtils;

    @BeforeEach
    void setup() {
        jwtUtils = new JwtUtils(userDetailsService);
        jwtUtils.jwtSecret = "YWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWI=";
        jwtUtils.jwtExpirationMs = 3600000;
        jwtUtils.jwtCookie = "jwt";
    }

    @Test
    void authEntryPointJwt_writesUnauthorizedJson() throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ServletOutputStream servletOutputStream = new ServletOutputStream() {
            @Override public void write(int b) throws IOException { outputStream.write(b); }
        };

        when(response.getOutputStream()).thenReturn(servletOutputStream);
        when(request.getServletPath()).thenReturn("/test");

        authEntryPointJwt.commence(request, response, new RuntimeException("Invalid"));

        String json = outputStream.toString();
        assertTrue(json.contains("\"status\":401"));
        assertTrue(json.contains("\"error\":\"Unauthorized\""));
        assertTrue(json.contains("\"message\":\"Invalid\""));
        assertTrue(json.contains("\"path\":\"/test\""));
    }

    @Test
    void authTokenFilter_setsSecurityContext() throws Exception {
        String token = jwtUtils.generateTokenFromUsername("user1");

        AuthTokenFilter filter = new AuthTokenFilter(jwtUtils, userDetailsService);

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

        UserDetails details = User.withUsername("user1").password("x").authorities("ROLE_USER").build();
        when(userDetailsService.loadUserByUsername("user1")).thenReturn(details);

        filter.doFilterInternal(request, response, filterChain);

        assertEquals("user1",
                org.springframework.security.core.context.SecurityContextHolder.getContext()
                        .getAuthentication().getName()
        );
    }

    @Test
    void jwtUtils_generatesValidToken() {
        String token = jwtUtils.generateTokenFromUsername("vishnu");
        assertNotNull(token);
        assertTrue(jwtUtils.validateJwtToken(token));
        assertEquals("vishnu", jwtUtils.getUserNameFromJwtToken(token));
    }

    @Test
    void jwtUtils_getJwtFromCookies_success() {
        Cookie cookie = new Cookie("jwt", "abc123");
        when(request.getCookies()).thenReturn(new Cookie[]{cookie});
        assertEquals("abc123", jwtUtils.getJwtFromCookies(request));
    }

    @Test
    void jwtUtils_getJwtFromCookies_nullIfMissing() {
        when(request.getCookies()).thenReturn(null);
        assertNull(jwtUtils.getJwtFromCookies(request));
    }

    @Test
    void jwtUtils_rejectsExpiredToken() {
        String expired = io.jsonwebtoken.Jwts.builder()
                .setSubject("abc")
                .setIssuedAt(new Date())
                .setExpiration(Date.from(Instant.now().minusSeconds(5)))
                .signWith(io.jsonwebtoken.security.Keys.hmacShaKeyFor(
                        java.util.Base64.getDecoder().decode(jwtUtils.jwtSecret)
                ), io.jsonwebtoken.SignatureAlgorithm.HS256)
                .compact();

        assertFalse(jwtUtils.validateJwtToken(expired));
    }
}

