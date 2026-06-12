package com.comptafx.dao;

import com.comptafx.entities.CompteBancaire;
import com.comptafx.entities.TransactionBancaire;
import java.util.List;
import java.util.Optional;

public interface ICompteBancaireDAO {
    CompteBancaire save(CompteBancaire compte) throws DatabaseException;
    Optional<CompteBancaire> findById(Long id) throws DatabaseException;
    List<CompteBancaire> findAll() throws DatabaseException;
    void update(CompteBancaire compte) throws DatabaseException;
    void delete(Long id) throws DatabaseException;

    TransactionBancaire saveTransaction(TransactionBancaire transaction) throws DatabaseException;
    List<TransactionBancaire> findTransactionsByCompte(Long compteId) throws DatabaseException;
    void deleteTransaction(Long id) throws DatabaseException;
}
