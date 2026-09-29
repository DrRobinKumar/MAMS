package com.kristalball.mams.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

// A user who can login to the system
@Entity
@Data
@NoArgsConstructor
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    // we never send the password in the API response
    @JsonIgnore
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    // admin has no base (null), other users belong to one base
    @ManyToOne
    private Base base;
}
