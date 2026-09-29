package com.kristalball.mams.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

// Keeps the history of transfers between two bases
@Entity
@Data
@NoArgsConstructor
public class Transfer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Base fromBase;

    @ManyToOne
    private Base toBase;

    @Enumerated(EnumType.STRING)
    private EquipmentType equipmentType;

    private String assetName;

    private int quantity;

    private String createdBy;

    private Instant createdAt = Instant.now();
}
