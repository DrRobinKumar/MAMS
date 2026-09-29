package com.kristalball.mams.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

// A military base (example: Base Alpha)
@Entity
@Data
@NoArgsConstructor
public class Base {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    public Base(String name) {
        this.name = name;
    }
}
