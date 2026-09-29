package com.kristalball.mams.dto;

import com.kristalball.mams.model.EquipmentType;
import lombok.Data;

// Data we get from the transfer form
@Data
public class TransferRequest {
    private Long fromBaseId;
    private Long toBaseId;
    private EquipmentType equipmentType;
    private String assetName;
    private int quantity;
}
