package com.comptafx.entities;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class TransactionBancaire {

    private Long id;
    private Long compteBancaireId;
    private LocalDate dateTransaction;
    private String libelle;
    private BigDecimal debit;
    private BigDecimal credit;
    private String reference;
    private TypeTransaction type;
    private String notes;
    private LocalDateTime createdAt;

    public TransactionBancaire() {
        this.debit = BigDecimal.ZERO;
        this.credit = BigDecimal.ZERO;
        this.type = TypeTransaction.VIREMENT;
        this.dateTransaction = LocalDate.now();
    }

    public BigDecimal getMontantNet() {
        return credit.subtract(debit);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCompteBancaireId() { return compteBancaireId; }
    public void setCompteBancaireId(Long compteBancaireId) { this.compteBancaireId = compteBancaireId; }

    public LocalDate getDateTransaction() { return dateTransaction; }
    public void setDateTransaction(LocalDate dateTransaction) { this.dateTransaction = dateTransaction; }

    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }

    public BigDecimal getDebit() { return debit; }
    public void setDebit(BigDecimal debit) { this.debit = debit; }

    public BigDecimal getCredit() { return credit; }
    public void setCredit(BigDecimal credit) { this.credit = credit; }

    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }

    public TypeTransaction getType() { return type; }
    public void setType(TypeTransaction type) { this.type = type; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransactionBancaire that = (TransactionBancaire) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
