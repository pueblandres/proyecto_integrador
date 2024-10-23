package com.dh.auth.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column( nullable = false, unique = true)
    private String email;
    @Column( nullable = false)
    private String password;
    @Column( nullable = false, unique = true)
    private String dni;
    @Column( nullable = false)
    private String firstName;
    @Column(nullable = false)
    private String lastName;
    private String phone;

}

