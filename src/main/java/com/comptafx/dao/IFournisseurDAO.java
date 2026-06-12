package com.comptafx.dao;

import com.comptafx.entities.Fournisseur;
import java.util.List;
import java.util.Optional;

public interface IFournisseurDAO {
    Fournisseur save(Fournisseur fournisseur) throws DatabaseException;
    Optional<Fournisseur> findById(Long id) throws DatabaseException;
    List<Fournisseur> findAll() throws DatabaseException;
    List<Fournisseur> findActifs() throws DatabaseException;
    void update(Fournisseur fournisseur) throws DatabaseException;
    void delete(Long id) throws DatabaseException;
    long count() throws DatabaseException;
    boolean existsById(Long id) throws DatabaseException;
    String generateCode() throws DatabaseException;
}
