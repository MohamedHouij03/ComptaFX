package com.comptafx.metier;

import com.comptafx.entities.CompteBancaire;
import com.comptafx.entities.TransactionBancaire;
import java.math.BigDecimal;
import java.util.List;

public interface ServiceTresorerie {
    CompteBancaire creerCompte(CompteBancaire compte) throws ComptaException;
    List<CompteBancaire> getTousLesComptes() throws ComptaException;
    CompteBancaire modifierCompte(CompteBancaire compte) throws ComptaException;
    void supprimerCompte(Long id) throws ComptaException;

    TransactionBancaire ajouterTransaction(TransactionBancaire transaction) throws ComptaException;
    List<TransactionBancaire> getTransactions(Long compteId) throws ComptaException;
    void supprimerTransaction(Long id) throws ComptaException;

    BigDecimal getTotalSoldes() throws ComptaException;
}
