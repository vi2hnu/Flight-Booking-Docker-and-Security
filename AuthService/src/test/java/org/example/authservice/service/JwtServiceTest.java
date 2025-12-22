package org.example.authservice.service;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.authservice.service.jwt.AuthEntryPointJwt;
import org.example.authservice.service.jwt.AuthTokenFilter;
import org.example.authservice.service.jwt.JwtUtils;
import org.example.authservice.service.user.UserDetailsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.Date;

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
        SecurityContextHolder.clearContext();

        jwtUtils = new JwtUtils(userDetailsService);

        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", "YWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWI=");
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", 3600000L);
        ReflectionTestUtils.setField(jwtUtils, "jwtCookie", "jwt");
    }

    @Test
    void authEntryPointJwt_writesUnauthorizedJson() throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ServletOutputStream servletOutputStream = new ServletOutputStream() {
            @Override
            public void write(int b) throws IOException {
                outputStream.write(b);
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setWriteListener(WriteListener writeListener) {
            }
        };

        when(response.getOutputStream()).thenReturn(servletOutputStream);
        when(request.getServletPath()).thenReturn("/test");

        org.springframework.security.core.AuthenticationException authException =
                new org.springframework.security.authentication.BadCredentialsException("Invalid");

        authEntryPointJwt.commence(request, response, authException);

        String json = outputStream.toString();
        assertTrue(json.contains("\"status\":401"));
        assertTrue(json.contains("\"error\":\"Unauthorized\""));
        assertTrue(json.contains("\"message\":\"Invalid\""));
        assertTrue(json.contains("\"path\":\"/test\""));

        verify(response).setContentType("application/json");
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }


    @Test
    void authTokenFilter_continuesFilterChainWhenNoToken() throws Exception {
        AuthTokenFilter filter = new AuthTokenFilter(jwtUtils, userDetailsService);

        filter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void authTokenFilter_continuesFilterChainOnInvalidToken() throws Exception {
        AuthTokenFilter filter = new AuthTokenFilter(jwtUtils, userDetailsService);

        filter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
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
    void jwtUtils_getJwtFromCookies_nullIfWrongCookieName() {
        Cookie cookie = new Cookie("other_cookie", "abc123");
        when(request.getCookies()).thenReturn(new Cookie[]{cookie});

        assertNull(jwtUtils.getJwtFromCookies(request));
    }

    @Test
    void jwtUtils_rejectsExpiredToken() {
        String jwtSecret = "YWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWI=";

        String expired = io.jsonwebtoken.Jwts.builder()
                .setSubject("abc")
                .setIssuedAt(new Date())
                .setExpiration(Date.from(Instant.now().minusSeconds(5)))
                .signWith(io.jsonwebtoken.security.Keys.hmacShaKeyFor(
                        java.util.Base64.getDecoder().decode(jwtSecret)
                ), io.jsonwebtoken.SignatureAlgorithm.HS256)
                .compact();

        assertFalse(jwtUtils.validateJwtToken(expired));
    }

    @Test
    void jwtUtils_rejectsMalformedToken() {
        assertFalse(jwtUtils.validateJwtToken("not.a.valid.token"));
    }

    @Test
    void jwtUtils_rejectsEmptyToken() {
        assertFalse(jwtUtils.validateJwtToken(""));
    }

    @Test
    void jwtUtils_rejectsNullToken() {
        assertFalse(jwtUtils.validateJwtToken(null));
    }
}