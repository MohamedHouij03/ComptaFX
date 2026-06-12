package com.comptafx.entities;

import com.comptafx.metier.Auditable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class Client implements Auditable {

    private Long id;
    private String code;
    private String nom;
    private TypeClient type;
    private String email;
    private String telephone;
    private String adresse;
    private String ville;
    private String codePostal;
    private String pays;
    private String matriculeFiscal;
    private String rib;
    private BigDecimal limiteCredit;
    private BigDecimal solde;
    private String notes;
    private boolean actif;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

    public Client() {
        this.type = TypeClient.ENTREPRISE;
        this.limiteCredit = BigDecimal.ZERO;
        this.solde = BigDecimal.ZERO;
        this.actif = true;
        this.pays = "Tunisie";
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public TypeClient getType() { return type; }
    public void setType(TypeClient type) { this.type = type; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }

    public String getVille() { return ville; }
    public void setVille(String ville) { this.ville = ville; }

    public String getCodePostal() { return codePostal; }
    public void setCodePostal(String codePostal) { this.codePostal = codePostal; }

    public String getPays() { return pays; }
    public void setPays(String pays) { this.pays = pays; }

    public String getMatriculeFiscal() { return matriculeFiscal; }
    public void setMatriculeFiscal(String matriculeFiscal) { this.matriculeFiscal = matriculeFiscal; }

    public String getRib() { return rib; }
    public void setRib(String rib) { this.rib = rib; }

    public BigDecimal getLimiteCredit() { return limiteCredit; }
    public void setLimiteCredit(BigDecimal limiteCredit) { this.limiteCredit = limiteCredit; }

    public BigDecimal getSolde() { return solde; }
    public void setSolde(BigDecimal solde) { this.solde = solde; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public boolean isActif() { return actif; }
    public void setActif(boolean actif) { this.actif = actif; }

    @Override public LocalDateTime getCreatedAt() { return createdAt; }
    @Override public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    @Override public LocalDateTime getUpdatedAt() { return updatedAt; }
    @Override public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    @Override public String getCreatedBy() { return createdBy; }
    @Override public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    @Override public String getUpdatedBy() { return updatedBy; }
    @Override public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Client client = (Client) o;
        return Objects.equals(id, client.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return nom != null ? nom : "";
    }
}
