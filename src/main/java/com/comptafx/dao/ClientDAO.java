package com.comptafx.dao;

import com.comptafx.entities.Client;
import com.comptafx.entities.TypeClient;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ClientDAO implements IClientDAO {

    @Override
    public Client save(Client client) throws DatabaseException {
        String sql = """
            INSERT INTO clients (code, nom, type, email, telephone, adresse, ville,
                code_postal, pays, matricule_fiscal, rib, limite_credit, solde,
                notes, actif, created_at, created_by)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;
        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);
            try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, client.getCode());
                pstmt.setString(2, client.getNom());
                pstmt.setString(3, client.getType().name());
                pstmt.setString(4, client.getEmail());
                pstmt.setString(5, client.getTelephone());
                pstmt.setString(6, client.getAdresse());
                pstmt.setString(7, client.getVille());
                pstmt.setString(8, client.getCodePostal());
                pstmt.setString(9, client.getPays());
                pstmt.setString(10, client.getMatriculeFiscal());
                pstmt.setString(11, client.getRib());
                pstmt.setBigDecimal(12, client.getLimiteCredit());
                pstmt.setBigDecimal(13, client.getSolde());
                pstmt.setString(14, client.getNotes());
                pstmt.setBoolean(15, client.isActif());
                pstmt.setTimestamp(16, Timestamp.valueOf(LocalDateTime.now()));
                pstmt.setString(17, client.getCreatedBy());
                pstmt.executeUpdate();
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) client.setId(rs.getLong(1));
                }
            }
            conn.commit();
            return client;
        } catch (SQLException e) {
            if (conn != null) { try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); } }
            throw new DatabaseException("Erreur lors de la sauvegarde du client", e);
        } finally {
            if (conn != null) { try { conn.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); } }
        }
    }

    @Override
    public Optional<Client> findById(Long id) throws DatabaseException {
        String sql = "SELECT * FROM clients WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors de la recherche du client", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Client> findAll() throws DatabaseException {
        String sql = "SELECT * FROM clients ORDER BY nom ASC";
        List<Client> list = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors du chargement des clients", e);
        }
        return list;
    }

    @Override
    public List<Client> findActifs() throws DatabaseException {
        return findAll().stream().filter(Client::isActif).collect(Collectors.toList());
    }

    @Override
    public void update(Client client) throws DatabaseException {
        String sql = """
            UPDATE clients SET nom=?, type=?, email=?, telephone=?, adresse=?, ville=?,
                code_postal=?, pays=?, matricule_fiscal=?, rib=?, limite_credit=?, solde=?,
                notes=?, actif=?, updated_at=?, updated_by=?
            WHERE id=?
        """;
        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, client.getNom());
                pstmt.setString(2, client.getType().name());
                pstmt.setString(3, client.getEmail());
                pstmt.setString(4, client.getTelephone());
                pstmt.setString(5, client.getAdresse());
                pstmt.setString(6, client.getVille());
                pstmt.setString(7, client.getCodePostal());
                pstmt.setString(8, client.getPays());
                pstmt.setString(9, client.getMatriculeFiscal());
                pstmt.setString(10, client.getRib());
                pstmt.setBigDecimal(11, client.getLimiteCredit());
                pstmt.setBigDecimal(12, client.getSolde());
                pstmt.setString(13, client.getNotes());
                pstmt.setBoolean(14, client.isActif());
                pstmt.setTimestamp(15, Timestamp.valueOf(LocalDateTime.now()));
                pstmt.setString(16, client.getUpdatedBy());
                pstmt.setLong(17, client.getId());
                pstmt.executeUpdate();
            }
            conn.commit();
        } catch (SQLException e) {
            if (conn != null) { try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); } }
            throw new DatabaseException("Erreur lors de la mise à jour du client", e);
        } finally {
            if (conn != null) { try { conn.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); } }
        }
    }

    @Override
    public void delete(Long id) throws DatabaseException {
        String sql = "DELETE FROM clients WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            int rows = pstmt.executeUpdate();
            if (rows == 0) throw new DatabaseException("Client introuvable: " + id);
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors de la suppression du client", e);
        }
    }

    @Override
    public long count() throws DatabaseException {
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM clients")) {
            if (rs.next()) return rs.getLong(1);
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors du comptage des clients", e);
        }
        return 0;
    }

    @Override
    public boolean existsById(Long id) throws DatabaseException {
        return findById(id).isPresent();
    }

    @Override
    public String generateCode() throws DatabaseException {
        long count = count() + 1;
        return String.format("CLI-%05d", count);
    }

    private Client map(ResultSet rs) throws SQLException {
        Client c = new Client();
        c.setId(rs.getLong("id"));
        c.setCode(rs.getString("code"));
        c.setNom(rs.getString("nom"));
        c.setType(TypeClient.valueOf(rs.getString("type")));
        c.setEmail(rs.getString("email"));
        c.setTelephone(rs.getString("telephone"));
        c.setAdresse(rs.getString("adresse"));
        c.setVille(rs.getString("ville"));
        c.setCodePostal(rs.getString("code_postal"));
        c.setPays(rs.getString("pays"));
        c.setMatriculeFiscal(rs.getString("matricule_fiscal"));
        c.setRib(rs.getString("rib"));
        c.setLimiteCredit(rs.getBigDecimal("limite_credit"));
        c.setSolde(rs.getBigDecimal("solde"));
        c.setNotes(rs.getString("notes"));
        c.setActif(rs.getBoolean("actif"));
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) c.setCreatedAt(createdAt.toLocalDateTime());
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) c.setUpdatedAt(updatedAt.toLocalDateTime());
        c.setCreatedBy(rs.getString("created_by"));
        c.setUpdatedBy(rs.getString("updated_by"));
        return c;
    }
}
