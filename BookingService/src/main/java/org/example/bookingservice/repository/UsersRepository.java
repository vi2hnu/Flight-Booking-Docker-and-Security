package org.example.bookingservice.repository;

import org.example.bookingservice.model.entity.Users;
import org.springframework.data.repository.CrudRepository;

public interface UsersRepository extends CrudRepository<Users, Long> {
    Users findByEmail(String email);
    Users getUsersById(Long id);
}
