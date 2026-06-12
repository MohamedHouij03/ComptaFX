package com.comptafx.metier;

import com.comptafx.dao.FournisseurDAO;
import com.comptafx.entities.Fournisseur;

import java.util.List;
import java.util.stream.Collectors;

public class ServiceFournisseursImpl implements ServiceFournisseurs {

    private final FournisseurDAO fournisseurDAO = new FournisseurDAO();

    @Override
    public Fournisseur creerFournisseur(Fournisseur fournisseur) throws ComptaException {
        validerFournisseur(fournisseur);
        if (fournisseur.getCode() == null || fournisseur.getCode().isBlank()) {
            fournisseur.setCode(fournisseurDAO.generateCode());
        }
        return fournisseurDAO.save(fournisseur);
    }

    @Override
    public Fournisseur getFournisseurById(Long id) throws ComptaException {
        return fournisseurDAO.findById(id)
                .orElseThrow(() -> new ComptaException("Fournisseur introuvable: " + id));
    }

    @Override
    public List<Fournisseur> getTousLesFournisseurs() throws ComptaException {
        return fournisseurDAO.findAll();
    }

    @Override
    public List<Fournisseur> getFournisseursActifs() throws ComptaException {
        return fournisseurDAO.findActifs();
    }

    @Override
    public Fournisseur modifierFournisseur(Fournisseur fournisseur) throws ComptaException {
        validerFournisseur(fournisseur);
        fournisseurDAO.update(fournisseur);
        return fournisseur;
    }

    @Override
    public void supprimerFournisseur(Long id) throws ComptaException {
        fournisseurDAO.findById(id)
                .orElseThrow(() -> new ComptaException("Fournisseur introuvable: " + id));
        fournisseurDAO.delete(id);
    }

    @Override
    public List<Fournisseur> rechercherFournisseurs(String terme) throws ComptaException {
        String t = terme == null ? "" : terme.toLowerCase();
        return getTousLesFournisseurs().stream()
                .filter(f -> t.isBlank()
                        || (f.getNom() != null && f.getNom().toLowerCase().contains(t))
                        || (f.getCode() != null && f.getCode().toLowerCase().contains(t))
                        || (f.getEmail() != null && f.getEmail().toLowerCase().contains(t))
                        || (f.getTelephone() != null && f.getTelephone().contains(t))
                        || (f.getVille() != null && f.getVille().toLowerCase().contains(t))
                        || (f.getCategorie() != null && f.getCategorie().toLowerCase().contains(t)))
                .sorted((a, b) -> a.getNom().compareToIgnoreCase(b.getNom()))
                .collect(Collectors.toList());
    }

    private void validerFournisseur(Fournisseur fournisseur) throws ComptaException {
        if (fournisseur.getNom() == null || fournisseur.getNom().isBlank()) {
            throw new ComptaException("Le nom du fournisseur est obligatoire.");
        }
        if (fournisseur.getType() == null) {
            throw new ComptaException("Le type de fournisseur est obligatoire.");
        }
    }
}
