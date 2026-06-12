package com.comptafx.dao;

import com.comptafx.entities.Client;
import java.util.List;
import java.util.Optional;

public interface IClientDAO {
    Client save(Client client) throws DatabaseException;
    Optional<Client> findById(Long id) throws DatabaseException;
    List<Client> findAll() throws DatabaseException;
    List<Client> findActifs() throws DatabaseException;
    void update(Client client) throws DatabaseException;
    void delete(Long id) throws DatabaseException;
    long count() throws DatabaseException;
    boolean existsById(Long id) throws DatabaseException;
    String generateCode() throws DatabaseException;
}
