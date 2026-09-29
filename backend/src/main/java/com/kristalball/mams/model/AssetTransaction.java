package com.kristalball.mams.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;

// One table for everything: purchase, transfer in/out, assigned and expended.
// Each of these is saved as one row and we never edit or delete the rows.
@Entity
@Data
@NoArgsConstructor
@Table(indexes = {
        @Index(columnList = "base_id,txnDate"),
        @Index(columnList = "type")
})
public class AssetTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @Enumerated(EnumType.STRING)
    private EquipmentType equipmentType;

    private String assetName;

    private int quantity;

    @ManyToOne(optional = false)
    private Base base;

    private LocalDate txnDate;

    // only used for assignments and expenditures
    private String personnel;

    private String createdBy;

    // only filled when this row came from a transfer
    private Long transferId;

    private Instant createdAt = Instant.now();

    // How much this row changes the stock of the base.
    // Purchase and transfer in add stock, transfer out and expended reduce stock.
    // Assigned does not change the balance.
    public int stockEffect() {
        if (type == TransactionType.PURCHASE || type == TransactionType.TRANSFER_IN) {
            return quantity;
        }
        if (type == TransactionType.TRANSFER_OUT || type == TransactionType.EXPENDED) {
            return -quantity;
        }
        return 0;
    }
}
