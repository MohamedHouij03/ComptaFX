package com.comptafx.metier;

import com.comptafx.dao.ClientDAO;
import com.comptafx.entities.Client;

import java.util.List;
import java.util.stream.Collectors;

public class ServiceClientsImpl implements ServiceClients {

    private final ClientDAO clientDAO = new ClientDAO();

    @Override
    public Client creerClient(Client client) throws ComptaException {
        validerClient(client);
        if (client.getCode() == null || client.getCode().isBlank()) {
            client.setCode(clientDAO.generateCode());
        }
        return clientDAO.save(client);
    }

    @Override
    public Client getClientById(Long id) throws ComptaException {
        return clientDAO.findById(id)
                .orElseThrow(() -> new ComptaException("Client introuvable: " + id));
    }

    @Override
    public List<Client> getTousLesClients() throws ComptaException {
        return clientDAO.findAll();
    }

    @Override
    public List<Client> getClientsActifs() throws ComptaException {
        return clientDAO.findActifs();
    }

    @Override
    public Client modifierClient(Client client) throws ComptaException {
        validerClient(client);
        clientDAO.update(client);
        return client;
    }

    @Override
    public void supprimerClient(Long id) throws ComptaException {
        clientDAO.findById(id)
                .orElseThrow(() -> new ComptaException("Client introuvable: " + id));
        clientDAO.delete(id);
    }

    @Override
    public List<Client> rechercherClients(String terme) throws ComptaException {
        String t = terme == null ? "" : terme.toLowerCase();
        return getTousLesClients().stream()
                .filter(c -> t.isBlank()
                        || (c.getNom() != null && c.getNom().toLowerCase().contains(t))
                        || (c.getCode() != null && c.getCode().toLowerCase().contains(t))
                        || (c.getEmail() != null && c.getEmail().toLowerCase().contains(t))
                        || (c.getTelephone() != null && c.getTelephone().contains(t))
                        || (c.getVille() != null && c.getVille().toLowerCase().contains(t)))
                .sorted((a, b) -> a.getNom().compareToIgnoreCase(b.getNom()))
                .collect(Collectors.toList());
    }

    private void validerClient(Client client) throws ComptaException {
        if (client.getNom() == null || client.getNom().isBlank()) {
            throw new ComptaException("Le nom du client est obligatoire.");
        }
        if (client.getType() == null) {
            throw new ComptaException("Le type de client est obligatoire.");
        }
    }
}
