package org.example.authservice.service.auth;

import org.example.authservice.model.entity.Users;
import org.example.authservice.repository.UsersRepository;
import org.example.authservice.service.jwt.JwtUtils;
import org.example.authservice.service.user.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = new DefaultOAuth2UserService().loadUser(userRequest);

        Users user = usersRepository.findUsersByEmail(oauth2User.getAttribute("email"));

        if(user==null){
            user = new Users();
            user.setUsername(oauth2User.getAttribute("email"));
            user.setPassword("");
        }
        UserDetailsImpl userDetails = UserDetailsImpl.build(user);
        String token  = jwtUtils.getJwtToken(userDetails);
        Map<String,Object> attributes = new HashMap<>(oauth2User.getAttributes());
        attributes.put("token", token);

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("USER")),
                attributes,
                "sub"
        );
    }
}
