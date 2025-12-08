package org.example.emailservice.model.entity;

import lombok.Data;
import org.example.emailservice.model.enums.Gender;

@Data
public class Users {
    private Long id;
    String name;
    String email;
    Gender gender;
}
