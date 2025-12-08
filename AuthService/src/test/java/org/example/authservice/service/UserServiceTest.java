package org.example.authservice.service;


import org.example.authservice.model.entity.Role;
import org.example.authservice.model.entity.Role;
import org.example.authservice.model.entity.Users;
import org.example.authservice.model.enums.eRole;
import org.example.authservice.repository.UsersRepository;
import org.example.authservice.service.user.UserDetailsImpl;
import org.example.authservice.service.user.UserDetailsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UsersRepository usersRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    private Users testUser;

    @BeforeEach
    void setup() {
        Role role = new Role();
        role.setName(eRole.USER);

        testUser = new Users();
        testUser.setId(1L);
        testUser.setUsername("vishnu");
        testUser.setEmail("vishnu@mail.com");
        testUser.setPassword("pass");
        testUser.setRoles(Set.of(role));
    }

    @Test
    void userDetailsImpl_build_returnsCorrectAuthorities() {
        UserDetailsImpl userDetails = UserDetailsImpl.build(testUser);

        assertEquals(testUser.getId(), userDetails.getId());
        assertEquals(testUser.getUsername(), userDetails.getUsername());
        assertEquals(testUser.getEmail(), userDetails.getEmail());
        assertEquals(testUser.getPassword(), userDetails.getPassword());

        List<String> authorities = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        assertEquals(1, authorities.size());
        assertTrue(authorities.contains("ROLE_USER"));
    }

    @Test
    void userDetailsService_loadUserByUsername_success() {
        when(usersRepository.findUsersByUsername("vishnu")).thenReturn(testUser);

        UserDetails userDetails = userDetailsService.loadUserByUsername("vishnu");

        assertNotNull(userDetails);
        assertEquals("vishnu", userDetails.getUsername());
        assertEquals("pass", userDetails.getPassword());
        List<String> authorities = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        assertEquals(1, authorities.size());
        assertTrue(authorities.contains("ROLE_USER"));

        verify(usersRepository).findUsersByUsername("vishnu");
    }

    @Test
    void userDetailsService_loadUserByUsername_throwsIfNotFound() {
        when(usersRepository.findUsersByUsername("unknown")).thenReturn(null);

        assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("unknown"));

        verify(usersRepository).findUsersByUsername("unknown");
    }
}
