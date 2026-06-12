package com.comptafx.dao;

import com.comptafx.entities.CompteBancaire;
import com.comptafx.entities.TransactionBancaire;
import com.comptafx.entities.TypeTransaction;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CompteBancaireDAO implements ICompteBancaireDAO {

    @Override
    public CompteBancaire save(CompteBancaire c) throws DatabaseException {
        String sql = """
            INSERT INTO comptes_bancaires (banque, intitule, numero_compte, rib,
                solde_initial, solde_actuel, devise, actif, notes, created_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;
        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);
            try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, c.getBanque());
                pstmt.setString(2, c.getIntitule());
                pstmt.setString(3, c.getNumeroCompte());
                pstmt.setString(4, c.getRib());
                pstmt.setBigDecimal(5, c.getSoldeInitial());
                pstmt.setBigDecimal(6, c.getSoldeActuel());
                pstmt.setString(7, c.getDevise());
                pstmt.setBoolean(8, c.isActif());
                pstmt.setString(9, c.getNotes());
                pstmt.setTimestamp(10, Timestamp.valueOf(LocalDateTime.now()));
                pstmt.executeUpdate();
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) c.setId(rs.getLong(1));
                }
            }
            conn.commit();
            return c;
        } catch (SQLException e) {
            if (conn != null) { try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); } }
            throw new DatabaseException("Erreur lors de la sauvegarde du compte bancaire", e);
        } finally {
            if (conn != null) { try { conn.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); } }
        }
    }

    @Override
    public Optional<CompteBancaire> findById(Long id) throws DatabaseException {
        String sql = "SELECT * FROM comptes_bancaires WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return Optional.of(mapCompte(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors de la recherche du compte", e);
        }
        return Optional.empty();
    }

    @Override
    public List<CompteBancaire> findAll() throws DatabaseException {
        String sql = "SELECT * FROM comptes_bancaires ORDER BY intitule ASC";
        List<CompteBancaire> list = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapCompte(rs));
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors du chargement des comptes", e);
        }
        return list;
    }

    @Override
    public void update(CompteBancaire c) throws DatabaseException {
        String sql = """
            UPDATE comptes_bancaires SET banque=?, intitule=?, numero_compte=?, rib=?,
                solde_initial=?, solde_actuel=?, devise=?, actif=?, notes=?, updated_at=?
            WHERE id=?
        """;
        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, c.getBanque());
                pstmt.setString(2, c.getIntitule());
                pstmt.setString(3, c.getNumeroCompte());
                pstmt.setString(4, c.getRib());
                pstmt.setBigDecimal(5, c.getSoldeInitial());
                pstmt.setBigDecimal(6, c.getSoldeActuel());
                pstmt.setString(7, c.getDevise());
                pstmt.setBoolean(8, c.isActif());
                pstmt.setString(9, c.getNotes());
                pstmt.setTimestamp(10, Timestamp.valueOf(LocalDateTime.now()));
                pstmt.setLong(11, c.getId());
                pstmt.executeUpdate();
            }
            conn.commit();
        } catch (SQLException e) {
            if (conn != null) { try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); } }
            throw new DatabaseException("Erreur lors de la mise à jour du compte", e);
        } finally {
            if (conn != null) { try { conn.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); } }
        }
    }

    @Override
    public void delete(Long id) throws DatabaseException {
        String sql = "DELETE FROM comptes_bancaires WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors de la suppression du compte", e);
        }
    }

    @Override
    public TransactionBancaire saveTransaction(TransactionBancaire t) throws DatabaseException {
        String sql = """
            INSERT INTO transactions_bancaires (compte_bancaire_id, date_transaction,
                libelle, debit, credit, reference, type, notes, created_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;
        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);
            try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setLong(1, t.getCompteBancaireId());
                pstmt.setDate(2, Date.valueOf(t.getDateTransaction()));
                pstmt.setString(3, t.getLibelle());
                pstmt.setBigDecimal(4, t.getDebit());
                pstmt.setBigDecimal(5, t.getCredit());
                pstmt.setString(6, t.getReference());
                pstmt.setString(7, t.getType().name());
                pstmt.setString(8, t.getNotes());
                pstmt.setTimestamp(9, Timestamp.valueOf(LocalDateTime.now()));
                pstmt.executeUpdate();
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) t.setId(rs.getLong(1));
                }
            }
            // Update solde actuel
            updateSoldeActuel(conn, t.getCompteBancaireId());
            conn.commit();
            return t;
        } catch (SQLException e) {
            if (conn != null) { try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); } }
            throw new DatabaseException("Erreur lors de la sauvegarde de la transaction", e);
        } finally {
            if (conn != null) { try { conn.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); } }
        }
    }

    private void updateSoldeActuel(Connection conn, Long compteId) throws SQLException {
        String sql = """
            UPDATE comptes_bancaires cb
            SET solde_actuel = cb.solde_initial + COALESCE(
                (SELECT SUM(credit) - SUM(debit) FROM transactions_bancaires WHERE compte_bancaire_id = cb.id), 0)
            WHERE cb.id = ?
        """;
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, compteId);
            pstmt.executeUpdate();
        }
    }

    @Override
    public List<TransactionBancaire> findTransactionsByCompte(Long compteId) throws DatabaseException {
        String sql = "SELECT * FROM transactions_bancaires WHERE compte_bancaire_id = ? ORDER BY date_transaction DESC";
        List<TransactionBancaire> list = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, compteId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) list.add(mapTransaction(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors du chargement des transactions", e);
        }
        return list;
    }

    @Override
    public void deleteTransaction(Long id) throws DatabaseException {
        // Get compteId before deleting for solde recalculation
        String getCompte = "SELECT compte_bancaire_id FROM transactions_bancaires WHERE id = ?";
        Long compteId = null;
        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(getCompte)) {
                ps.setLong(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) compteId = rs.getLong(1);
                }
            }
            String sql = "DELETE FROM transactions_bancaires WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setLong(1, id);
                pstmt.executeUpdate();
            }
            if (compteId != null) updateSoldeActuel(conn, compteId);
            conn.commit();
        } catch (SQLException e) {
            if (conn != null) { try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); } }
            throw new DatabaseException("Erreur lors de la suppression de la transaction", e);
        } finally {
            if (conn != null) { try { conn.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); } }
        }
    }

    private CompteBancaire mapCompte(ResultSet rs) throws SQLException {
        CompteBancaire c = new CompteBancaire();
        c.setId(rs.getLong("id"));
        c.setBanque(rs.getString("banque"));
        c.setIntitule(rs.getString("intitule"));
        c.setNumeroCompte(rs.getString("numero_compte"));
        c.setRib(rs.getString("rib"));
        c.setSoldeInitial(rs.getBigDecimal("solde_initial"));
        c.setSoldeActuel(rs.getBigDecimal("solde_actuel"));
        c.setDevise(rs.getString("devise"));
        c.setActif(rs.getBoolean("actif"));
        c.setNotes(rs.getString("notes"));
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) c.setCreatedAt(createdAt.toLocalDateTime());
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) c.setUpdatedAt(updatedAt.toLocalDateTime());
        return c;
    }

    private TransactionBancaire mapTransaction(ResultSet rs) throws SQLException {
        TransactionBancaire t = new TransactionBancaire();
        t.setId(rs.getLong("id"));
        t.setCompteBancaireId(rs.getLong("compte_bancaire_id"));
        t.setDateTransaction(rs.getDate("date_transaction").toLocalDate());
        t.setLibelle(rs.getString("libelle"));
        t.setDebit(rs.getBigDecimal("debit"));
        t.setCredit(rs.getBigDecimal("credit"));
        t.setReference(rs.getString("reference"));
        t.setType(TypeTransaction.valueOf(rs.getString("type")));
        t.setNotes(rs.getString("notes"));
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) t.setCreatedAt(createdAt.toLocalDateTime());
        return t;
    }
}
