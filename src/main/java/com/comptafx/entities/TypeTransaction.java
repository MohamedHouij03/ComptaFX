package com.comptafx.entities;

public enum TypeTransaction {
    VIREMENT("Virement"),
    CHEQUE("Chèque"),
    ESPECES("Espèces"),
    PRELEVEMENT("Prélèvement"),
    CARTE("Carte Bancaire"),
    AUTRE("Autre");

    private final String displayName;

    TypeTransaction(String displayName) {
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
