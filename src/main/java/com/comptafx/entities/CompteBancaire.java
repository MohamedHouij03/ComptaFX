package com.comptafx.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class CompteBancaire {

    private Long id;
    private String banque;
    private String intitule;
    private String numeroCompte;
    private String rib;
    private BigDecimal soldeInitial;
    private BigDecimal soldeActuel;
    private String devise;
    private boolean actif;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public CompteBancaire() {
        this.soldeInitial = BigDecimal.ZERO;
        this.soldeActuel = BigDecimal.ZERO;
        this.devise = "TND";
        this.actif = true;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getBanque() { return banque; }
    public void setBanque(String banque) { this.banque = banque; }

    public String getIntitule() { return intitule; }
    public void setIntitule(String intitule) { this.intitule = intitule; }

    public String getNumeroCompte() { return numeroCompte; }
    public void setNumeroCompte(String numeroCompte) { this.numeroCompte = numeroCompte; }

    public String getRib() { return rib; }
    public void setRib(String rib) { this.rib = rib; }

    public BigDecimal getSoldeInitial() { return soldeInitial; }
    public void setSoldeInitial(BigDecimal soldeInitial) { this.soldeInitial = soldeInitial; }

    public BigDecimal getSoldeActuel() { return soldeActuel; }
    public void setSoldeActuel(BigDecimal soldeActuel) { this.soldeActuel = soldeActuel; }

    public String getDevise() { return devise; }
    public void setDevise(String devise) { this.devise = devise; }

    public boolean isActif() { return actif; }
    public void setActif(boolean actif) { this.actif = actif; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompteBancaire that = (CompteBancaire) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return intitule != null ? intitule : "";
    }
}
