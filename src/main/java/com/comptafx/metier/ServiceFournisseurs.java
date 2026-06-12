package com.comptafx.metier;

import com.comptafx.entities.Fournisseur;
import java.util.List;

public interface ServiceFournisseurs {
    Fournisseur creerFournisseur(Fournisseur fournisseur) throws ComptaException;
    Fournisseur getFournisseurById(Long id) throws ComptaException;
    List<Fournisseur> getTousLesFournisseurs() throws ComptaException;
    List<Fournisseur> getFournisseursActifs() throws ComptaException;
    Fournisseur modifierFournisseur(Fournisseur fournisseur) throws ComptaException;
    void supprimerFournisseur(Long id) throws ComptaException;
    List<Fournisseur> rechercherFournisseurs(String terme) throws ComptaException;
}
