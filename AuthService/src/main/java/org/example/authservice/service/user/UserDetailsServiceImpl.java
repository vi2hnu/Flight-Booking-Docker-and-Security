package org.example.authservice.service.user;

import lombok.extern.slf4j.Slf4j;
import org.example.authservice.model.entity.Users;
import org.example.authservice.repository.UsersRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UsersRepository usersRepository;

    public UserDetailsServiceImpl(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) {
        Users user = usersRepository.findUsersByUsername(username);
        log.info("user {}",user);
        if(user==null){
            log.error("User not found: {}",username);
            throw new UsernameNotFoundException("User Not found");
        }
        return UserDetailsImpl.build(user);
    }

    public Users getUserDetails(String username){
        return usersRepository.findUsersByUsername(username);
    }
}
