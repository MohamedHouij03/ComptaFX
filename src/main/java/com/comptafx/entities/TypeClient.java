package com.comptafx.entities;

public enum TypeClient {
    PARTICULIER("Particulier"),
    ENTREPRISE("Entreprise");

    private final String displayName;

    TypeClient(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
