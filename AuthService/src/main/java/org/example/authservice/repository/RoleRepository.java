package org.example.authservice.repository;

import org.example.authservice.model.entity.Role;
import org.example.authservice.model.enums.eRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
  Optional<Role> findByName(eRole name);
}
