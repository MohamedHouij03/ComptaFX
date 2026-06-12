package com.comptafx.entities;


public enum JournalEntryStatus {
    DRAFT("Brouillon"),
    PENDING("En Attente d'Approbation"),
    POSTED("Validée"),
    REVERSED("Contre-passée"),
    CANCELLED("Annulée");
    
    private final String displayName;
    
    JournalEntryStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}

