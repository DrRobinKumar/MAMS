package com.kristalball.mams.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

// API log - we save one row for every successful POST/PUT/PATCH/DELETE call
@Entity
@Data
@NoArgsConstructor
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String method;

    private String path;

    private int status;

    private Instant createdAt = Instant.now();
}
