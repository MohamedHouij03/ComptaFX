package com.comptafx.entities;


public enum InvoiceStatus {
    DRAFT("Brouillon"),
    SENT("Envoyée"),
    PARTIALLY_PAID("Partiellement Payée"),
    PAID("Payée"),
    OVERDUE("En Retard"),
    CANCELLED("Annulée"),
    DISPUTED("Litige");
    
    private final String displayName;
    
    InvoiceStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}

