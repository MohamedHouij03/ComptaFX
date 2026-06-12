package com.comptafx.entities;


public enum InvoiceType {
    RECEIVABLE("Créances Clients", "Facture Client"),
    PAYABLE("Dettes Fournisseurs", "Facture Fournisseur");
    
    private final String accountType;
    private final String displayName;
    
    InvoiceType(String accountType, String displayName) {
        this.accountType = accountType;
        this.displayName = displayName;
    }
    
    public String getAccountType() {
        return accountType;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}

