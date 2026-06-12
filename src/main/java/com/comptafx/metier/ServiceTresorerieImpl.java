package com.comptafx.metier;

import com.comptafx.dao.CompteBancaireDAO;
import com.comptafx.entities.CompteBancaire;
import com.comptafx.entities.TransactionBancaire;

import java.math.BigDecimal;
import java.util.List;

public class ServiceTresorerieImpl implements ServiceTresorerie {

    private final CompteBancaireDAO dao = new CompteBancaireDAO();

    @Override
    public CompteBancaire creerCompte(CompteBancaire compte) throws ComptaException {
        validerCompte(compte);
        // soldeActuel starts equal to soldeInitial
        compte.setSoldeActuel(compte.getSoldeInitial());
        return dao.save(compte);
    }

    @Override
    public List<CompteBancaire> getTousLesComptes() throws ComptaException {
        return dao.findAll();
    }

    @Override
    public CompteBancaire modifierCompte(CompteBancaire compte) throws ComptaException {
        validerCompte(compte);
        dao.update(compte);
        return compte;
    }

    @Override
    public void supprimerCompte(Long id) throws ComptaException {
        dao.delete(id);
    }

    @Override
    public TransactionBancaire ajouterTransaction(TransactionBancaire transaction) throws ComptaException {
        if (transaction.getCompteBancaireId() == null) {
            throw new ComptaException("Le compte bancaire est obligatoire.");
        }
        if (transaction.getLibelle() == null || transaction.getLibelle().isBlank()) {
            throw new ComptaException("Le libellé de la transaction est obligatoire.");
        }
        if (transaction.getDebit().compareTo(BigDecimal.ZERO) == 0
                && transaction.getCredit().compareTo(BigDecimal.ZERO) == 0) {
            throw new ComptaException("Le montant débit ou crédit doit être renseigné.");
        }
        return dao.saveTransaction(transaction);
    }

    @Override
    public List<TransactionBancaire> getTransactions(Long compteId) throws ComptaException {
        return dao.findTransactionsByCompte(compteId);
    }

    @Override
    public void supprimerTransaction(Long id) throws ComptaException {
        dao.deleteTransaction(id);
    }

    @Override
    public BigDecimal getTotalSoldes() throws ComptaException {
        return dao.findAll().stream()
                .filter(CompteBancaire::isActif)
                .map(CompteBancaire::getSoldeActuel)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void validerCompte(CompteBancaire compte) throws ComptaException {
        if (compte.getIntitule() == null || compte.getIntitule().isBlank()) {
            throw new ComptaException("L'intitulé du compte est obligatoire.");
        }
        if (compte.getBanque() == null || compte.getBanque().isBlank()) {
            throw new ComptaException("La banque est obligatoire.");
        }
    }
}
