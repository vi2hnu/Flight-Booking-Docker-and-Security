package org.example.authservice.service.auth;

import lombok.extern.slf4j.Slf4j;
import org.example.authservice.dto.*;
import org.example.authservice.model.entity.Role;
import org.example.authservice.model.entity.Users;
import org.example.authservice.model.enums.eRole;
import org.example.authservice.repository.RoleRepository;
import org.example.authservice.repository.UsersRepository;
import org.example.authservice.service.jwt.JwtUtils;
import org.example.authservice.service.user.UserDetailsImpl;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UsersRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;
    private final JwtUtils jwtUtils;

    public AuthService(AuthenticationManager authenticationManager, UsersRepository userRepository,
                       RoleRepository roleRepository, PasswordEncoder encoder, JwtUtils jwtUtils){
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
        this.jwtUtils = jwtUtils;
    }

    public AuthResult login(LoginDTO loginRequest){

        //username password check
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        //creation of jwt using user deatils as claims
        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);

        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return new AuthResult(jwtCookie,new UserInfoResponse(userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles));
    }

    public boolean signUp(SignupDTO signUpRequest){
        if (userRepository.existsByUsername(signUpRequest.username()) || userRepository.existsByEmail(signUpRequest.email())) {
            log.info("user exists");
            return false;
        }

        // Create new user's account
        Users user = new Users(signUpRequest.username(),
                signUpRequest.email(),
                encoder.encode(signUpRequest.password()));

        Set<String> strRoles = signUpRequest.role();
        Set<Role> roles = new HashSet<>();

        //role check
        if (strRoles == null) {
            Role userRole = roleRepository.findByName(eRole.USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(eRole.ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);

                        break;
                    case "staff":
                        Role modRole = roleRepository.findByName(eRole.STAFF)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(modRole);
                        break;
                    default:
                        Role userRole = roleRepository.findByName(eRole.USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });
        }

        user.setRoles(roles);
        userRepository.save(user);
        return true;
    }

    public Users getUser(GetUserDTO dto){
        Users user = userRepository.findUsersByUsername(dto.username());
        user.setPassword("");
        return user;
    }

    public void changePassword(ChangePasswordDTO request){
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(request.username(), request.oldPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        Users user = userRepository.findUsersByUsername(request.username());
        user.setPassword(encoder.encode(request.newPassword()));
        userRepository.save(user);
    }
}
