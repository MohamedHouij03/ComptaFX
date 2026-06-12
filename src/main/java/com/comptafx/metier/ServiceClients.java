package com.comptafx.metier;

import com.comptafx.entities.Client;
import java.util.List;

public interface ServiceClients {
    Client creerClient(Client client) throws ComptaException;
    Client getClientById(Long id) throws ComptaException;
    List<Client> getTousLesClients() throws ComptaException;
    List<Client> getClientsActifs() throws ComptaException;
    Client modifierClient(Client client) throws ComptaException;
    void supprimerClient(Long id) throws ComptaException;
    List<Client> rechercherClients(String terme) throws ComptaException;
}
