package org.example.authservice.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.example.authservice.model.enums.eRole;

@Entity
@Data
public class Role {
    @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Enumerated(EnumType.STRING)
  @Column(length = 20)
  private eRole name;

  public Role() {
  }

}
