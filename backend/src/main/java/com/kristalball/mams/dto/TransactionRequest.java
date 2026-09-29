package com.kristalball.mams.dto;

import com.kristalball.mams.model.EquipmentType;
import lombok.Data;

import java.time.LocalDate;

// Data we get when user adds a purchase, assignment or expenditure
@Data
public class TransactionRequest {
    private EquipmentType equipmentType;
    private String assetName;
    private int quantity;
    private Long baseId;
    private LocalDate date;
    private String personnel;
}
