package org.example.authservice.repository;

import org.example.authservice.model.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersRepository extends JpaRepository<Users,Long> {
    Users findUsersByUsername(String username);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);

    Users findUsersByEmail(String email);
}
