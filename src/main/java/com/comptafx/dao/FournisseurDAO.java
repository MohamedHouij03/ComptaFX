package com.comptafx.dao;

import com.comptafx.entities.Fournisseur;
import com.comptafx.entities.TypeClient;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class FournisseurDAO implements IFournisseurDAO {

    @Override
    public Fournisseur save(Fournisseur f) throws DatabaseException {
        String sql = """
            INSERT INTO fournisseurs (code, nom, type, email, telephone, adresse, ville,
                code_postal, pays, matricule_fiscal, rib, delai_paiement, solde,
                categorie, notes, actif, created_at, created_by)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;
        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);
            try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, f.getCode());
                pstmt.setString(2, f.getNom());
                pstmt.setString(3, f.getType().name());
                pstmt.setString(4, f.getEmail());
                pstmt.setString(5, f.getTelephone());
                pstmt.setString(6, f.getAdresse());
                pstmt.setString(7, f.getVille());
                pstmt.setString(8, f.getCodePostal());
                pstmt.setString(9, f.getPays());
                pstmt.setString(10, f.getMatriculeFiscal());
                pstmt.setString(11, f.getRib());
                pstmt.setInt(12, f.getDelaiPaiement());
                pstmt.setBigDecimal(13, f.getSolde());
                pstmt.setString(14, f.getCategorie());
                pstmt.setString(15, f.getNotes());
                pstmt.setBoolean(16, f.isActif());
                pstmt.setTimestamp(17, Timestamp.valueOf(LocalDateTime.now()));
                pstmt.setString(18, f.getCreatedBy());
                pstmt.executeUpdate();
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) f.setId(rs.getLong(1));
                }
            }
            conn.commit();
            return f;
        } catch (SQLException e) {
            if (conn != null) { try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); } }
            throw new DatabaseException("Erreur lors de la sauvegarde du fournisseur", e);
        } finally {
            if (conn != null) { try { conn.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); } }
        }
    }

    @Override
    public Optional<Fournisseur> findById(Long id) throws DatabaseException {
        String sql = "SELECT * FROM fournisseurs WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors de la recherche du fournisseur", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Fournisseur> findAll() throws DatabaseException {
        String sql = "SELECT * FROM fournisseurs ORDER BY nom ASC";
        List<Fournisseur> list = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors du chargement des fournisseurs", e);
        }
        return list;
    }

    @Override
    public List<Fournisseur> findActifs() throws DatabaseException {
        return findAll().stream().filter(Fournisseur::isActif).collect(Collectors.toList());
    }

    @Override
    public void update(Fournisseur f) throws DatabaseException {
        String sql = """
            UPDATE fournisseurs SET nom=?, type=?, email=?, telephone=?, adresse=?, ville=?,
                code_postal=?, pays=?, matricule_fiscal=?, rib=?, delai_paiement=?, solde=?,
                categorie=?, notes=?, actif=?, updated_at=?, updated_by=?
            WHERE id=?
        """;
        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, f.getNom());
                pstmt.setString(2, f.getType().name());
                pstmt.setString(3, f.getEmail());
                pstmt.setString(4, f.getTelephone());
                pstmt.setString(5, f.getAdresse());
                pstmt.setString(6, f.getVille());
                pstmt.setString(7, f.getCodePostal());
                pstmt.setString(8, f.getPays());
                pstmt.setString(9, f.getMatriculeFiscal());
                pstmt.setString(10, f.getRib());
                pstmt.setInt(11, f.getDelaiPaiement());
                pstmt.setBigDecimal(12, f.getSolde());
                pstmt.setString(13, f.getCategorie());
                pstmt.setString(14, f.getNotes());
                pstmt.setBoolean(15, f.isActif());
                pstmt.setTimestamp(16, Timestamp.valueOf(LocalDateTime.now()));
                pstmt.setString(17, f.getUpdatedBy());
                pstmt.setLong(18, f.getId());
                pstmt.executeUpdate();
            }
            conn.commit();
        } catch (SQLException e) {
            if (conn != null) { try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); } }
            throw new DatabaseException("Erreur lors de la mise à jour du fournisseur", e);
        } finally {
            if (conn != null) { try { conn.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); } }
        }
    }

    @Override
    public void delete(Long id) throws DatabaseException {
        String sql = "DELETE FROM fournisseurs WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            int rows = pstmt.executeUpdate();
            if (rows == 0) throw new DatabaseException("Fournisseur introuvable: " + id);
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors de la suppression du fournisseur", e);
        }
    }

    @Override
    public long count() throws DatabaseException {
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM fournisseurs")) {
            if (rs.next()) return rs.getLong(1);
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors du comptage des fournisseurs", e);
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
        return String.format("FOU-%05d", count);
    }

    private Fournisseur map(ResultSet rs) throws SQLException {
        Fournisseur f = new Fournisseur();
        f.setId(rs.getLong("id"));
        f.setCode(rs.getString("code"));
        f.setNom(rs.getString("nom"));
        f.setType(TypeClient.valueOf(rs.getString("type")));
        f.setEmail(rs.getString("email"));
        f.setTelephone(rs.getString("telephone"));
        f.setAdresse(rs.getString("adresse"));
        f.setVille(rs.getString("ville"));
        f.setCodePostal(rs.getString("code_postal"));
        f.setPays(rs.getString("pays"));
        f.setMatriculeFiscal(rs.getString("matricule_fiscal"));
        f.setRib(rs.getString("rib"));
        f.setDelaiPaiement(rs.getInt("delai_paiement"));
        f.setSolde(rs.getBigDecimal("solde"));
        f.setCategorie(rs.getString("categorie"));
        f.setNotes(rs.getString("notes"));
        f.setActif(rs.getBoolean("actif"));
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) f.setCreatedAt(createdAt.toLocalDateTime());
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) f.setUpdatedAt(updatedAt.toLocalDateTime());
        f.setCreatedBy(rs.getString("created_by"));
        f.setUpdatedBy(rs.getString("updated_by"));
        return f;
    }
}
