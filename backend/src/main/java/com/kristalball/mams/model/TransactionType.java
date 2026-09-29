package com.kristalball.mams.model;

// Every entry in the asset_transaction table has one of these types
public enum TransactionType {
    PURCHASE,
    TRANSFER_IN,
    TRANSFER_OUT,
    ASSIGNED,
    EXPENDED
}
