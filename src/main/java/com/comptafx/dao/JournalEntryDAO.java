package com.comptafx.dao;

import com.comptafx.entities.JournalEntry;
import com.comptafx.entities.JournalEntryLine;
import com.comptafx.entities.JournalEntryStatus;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Data Access Object for Journal Entry entities
 * Demonstrates use of Collections, Streams, and Lambdas
 */
public class JournalEntryDAO implements IJournalEntryDAO {
    
    
    private Long currentClientId = 1L;
    
    public JournalEntryDAO() {
        
    }
    
    public void setCurrentClientId(Long clientId) {
        this.currentClientId = clientId;
    }
    
    @Override
    public JournalEntry save(JournalEntry entry) throws DatabaseException {
        String sql = """
            INSERT INTO journal_entries (entry_number, entry_date, description, reference, status, 
                                         is_recurring, recurring_frequency, client_id, created_at, created_by)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;
        
        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, entry.getEntryNumber());
                pstmt.setDate(2, Date.valueOf(entry.getEntryDate()));
                pstmt.setString(3, entry.getDescription());
                pstmt.setString(4, entry.getReference());
                pstmt.setString(5, entry.getStatus().name());
                pstmt.setInt(6, entry.isRecurring() ? 1 : 0);
                pstmt.setString(7, entry.getRecurringFrequency());
                pstmt.setLong(8, currentClientId);
                pstmt.setTimestamp(9, Timestamp.valueOf(LocalDateTime.now()));
                pstmt.setString(10, entry.getCreatedBy());
                
                pstmt.executeUpdate();
                
                // Get the generated ID (MySQL supports RETURN_GENERATED_KEYS)
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        entry.setId(rs.getLong(1));
                    }
                }
            }
            
            // Save journal entry lines
            saveLines(conn, entry);
            
            conn.commit();
            return entry;
            
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new DatabaseException("Failed to save journal entry", e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    private void saveLines(Connection conn, JournalEntry entry) throws SQLException {
        String sql = """
            INSERT INTO journal_entry_lines (journal_entry_id, account_code, account_name, 
                                             description, debit, credit, line_number)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            int lineNumber = 1;
            for (JournalEntryLine line : entry.getLines()) {
                pstmt.setLong(1, entry.getId());
                pstmt.setString(2, line.getAccountCode());
                pstmt.setString(3, line.getAccountName());
                pstmt.setString(4, line.getDescription());
                pstmt.setBigDecimal(5, line.getDebit());
                pstmt.setBigDecimal(6, line.getCredit());
                pstmt.setInt(7, lineNumber++);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
    }
    
    @Override
    public Optional<JournalEntry> findById(Long id) throws DatabaseException {
        String sql = "SELECT * FROM journal_entries WHERE id = ?";
        JournalEntry entry = null;
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    entry = mapResultSetToEntry(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find journal entry", e);
        }
        
        if (entry != null) {
            entry.setLines(findLinesByEntryId(entry.getId()));
            return Optional.of(entry);
        }
        return Optional.empty();
    }
    
    private List<JournalEntryLine> findLinesByEntryId(Long entryId) throws DatabaseException {
        String sql = "SELECT * FROM journal_entry_lines WHERE journal_entry_id = ? ORDER BY line_number";
        List<JournalEntryLine> lines = new ArrayList<>();
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, entryId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    lines.add(mapResultSetToLine(rs));
                }
            }
            
            return lines;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find journal entry lines", e);
        }
    }
    
    @Override
    public List<JournalEntry> findAll() throws DatabaseException {
        String sql = "SELECT * FROM journal_entries WHERE client_id = ? ORDER BY entry_date DESC, id DESC";
        List<JournalEntry> entries = new ArrayList<>();
        
        // First, load all entries without lines
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, currentClientId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    JournalEntry entry = mapResultSetToEntry(rs);
                    entries.add(entry);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to retrieve journal entries", e);
        }
        
        // Then, load lines for each entry (separate queries)
        for (JournalEntry entry : entries) {
            entry.setLines(findLinesByEntryId(entry.getId()));
        }
        
        return entries;
    }
    
    /**
     * Find entries by date range using Streams
     */
    public List<JournalEntry> findByDateRange(LocalDate startDate, LocalDate endDate) throws DatabaseException {
        // Using Stream to filter by date range with Lambda
        return findAll().stream()
                .filter(entry -> !entry.getEntryDate().isBefore(startDate) && 
                                !entry.getEntryDate().isAfter(endDate))
                .sorted(Comparator.comparing(JournalEntry::getEntryDate))
                .collect(Collectors.toList());
    }
    
    /**
     * Find entries by account code using Streams
     */
    public List<JournalEntry> findByAccountCode(String accountCode) throws DatabaseException {
        // Using Stream with nested Lambda to filter by account
        return findAll().stream()
                .filter(entry -> entry.getLines().stream()
                        .anyMatch(line -> line.getAccountCode().equals(accountCode)))
                .collect(Collectors.toList());
    }
    
    /**
     * Get trial balance as Map using Streams
     */
    public Map<String, BigDecimal> getTrialBalance() throws DatabaseException {
        Map<String, BigDecimal> trialBalance = new LinkedHashMap<>();
        
        List<JournalEntry> postedEntries = findAll().stream()
                .filter(e -> e.getStatus() == JournalEntryStatus.POSTED)
                .collect(Collectors.toList());
        
        // Flatten all lines and group by account code
        postedEntries.stream()
                .flatMap(entry -> entry.getLines().stream())
                .collect(Collectors.groupingBy(
                        JournalEntryLine::getAccountCode,
                        LinkedHashMap::new,
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                line -> line.getDebit().subtract(line.getCredit()),
                                BigDecimal::add
                        )
                ))
                .forEach(trialBalance::put);
        
        return trialBalance;
    }
    
    /**
     * Get general ledger grouped by account
     */
    public Map<String, List<JournalEntryLine>> getLedger() throws DatabaseException {
        // Using Stream to group lines by account code
        return findAll().stream()
                .filter(e -> e.getStatus() == JournalEntryStatus.POSTED)
                .flatMap(entry -> entry.getLines().stream())
                .collect(Collectors.groupingBy(
                        JournalEntryLine::getAccountCode,
                        LinkedHashMap::new,
                        Collectors.toList()
                ));
    }
    
    /**
     * Calculate total debits for a period
     */
    public BigDecimal getTotalDebits(LocalDate startDate, LocalDate endDate) throws DatabaseException {
        // Using Stream with reduce for summing
        return findByDateRange(startDate, endDate).stream()
                .filter(e -> e.getStatus() == JournalEntryStatus.POSTED)
                .flatMap(entry -> entry.getLines().stream())
                .map(JournalEntryLine::getDebit)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    /**
     * Calculate total credits for a period
     */
    public BigDecimal getTotalCredits(LocalDate startDate, LocalDate endDate) throws DatabaseException {
        return findByDateRange(startDate, endDate).stream()
                .filter(e -> e.getStatus() == JournalEntryStatus.POSTED)
                .flatMap(entry -> entry.getLines().stream())
                .map(JournalEntryLine::getCredit)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    @Override
    public void update(JournalEntry entry) throws DatabaseException {
        String sql = """
            UPDATE journal_entries 
            SET entry_date = ?, description = ?, reference = ?, status = ?, 
                is_recurring = ?, recurring_frequency = ?, updated_at = ?, updated_by = ?
            WHERE id = ?
        """;
        
        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setDate(1, Date.valueOf(entry.getEntryDate()));
                pstmt.setString(2, entry.getDescription());
                pstmt.setString(3, entry.getReference());
                pstmt.setString(4, entry.getStatus().name());
                pstmt.setInt(5, entry.isRecurring() ? 1 : 0);
                pstmt.setString(6, entry.getRecurringFrequency());
                pstmt.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));
                pstmt.setString(8, entry.getUpdatedBy());
                pstmt.setLong(9, entry.getId());
                
                pstmt.executeUpdate();
            }
            
            // Delete existing lines and re-insert
            deleteLinesByEntryId(conn, entry.getId());
            saveLines(conn, entry);
            
            conn.commit();
            
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new DatabaseException("Failed to update journal entry", e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    private void deleteLinesByEntryId(Connection conn, Long entryId) throws SQLException {
        String sql = "DELETE FROM journal_entry_lines WHERE journal_entry_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, entryId);
            pstmt.executeUpdate();
        }
    }
    
    @Override
    public void delete(Long id) throws DatabaseException {
        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);
        
            // Delete lines first (due to foreign key constraint)
            deleteLinesByEntryId(conn, id);
            
            // Delete the entry
            String sql = "DELETE FROM journal_entries WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected == 0) {
                    throw new DatabaseException("Journal entry not found: " + id);
                }
            }
            
            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new DatabaseException("Failed to delete journal entry", e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    @Override
    public long count() throws DatabaseException {
        String sql = "SELECT COUNT(*) FROM journal_entries WHERE client_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, currentClientId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
            
            return 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to count journal entries", e);
        }
    }
    
    @Override
    public boolean existsById(Long id) throws DatabaseException {
        return findById(id).isPresent();
    }
    
    public String generateEntryNumber() throws DatabaseException {
        String prefix = "JE-" + LocalDate.now().getYear() + "-";
        long count = count() + 1;
        return String.format("%s%05d", prefix, count);
    }
    
    private JournalEntry mapResultSetToEntry(ResultSet rs) throws SQLException {
        JournalEntry entry = new JournalEntry();
        entry.setId(rs.getLong("id"));
        entry.setEntryNumber(rs.getString("entry_number"));
        entry.setEntryDate(rs.getDate("entry_date").toLocalDate());
        entry.setDescription(rs.getString("description"));
        entry.setReference(rs.getString("reference"));
        entry.setStatus(JournalEntryStatus.valueOf(rs.getString("status")));
        entry.setRecurring(rs.getInt("is_recurring") == 1);
        entry.setRecurringFrequency(rs.getString("recurring_frequency"));
        entry.setClientId(rs.getLong("client_id"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            entry.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            entry.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        entry.setCreatedBy(rs.getString("created_by"));
        entry.setUpdatedBy(rs.getString("updated_by"));
        
        return entry;
    }
    
    private JournalEntryLine mapResultSetToLine(ResultSet rs) throws SQLException {
        JournalEntryLine line = new JournalEntryLine();
        line.setId(rs.getLong("id"));
        line.setAccountCode(rs.getString("account_code"));
        line.setAccountName(rs.getString("account_name"));
        line.setDescription(rs.getString("description"));
        line.setDebit(rs.getBigDecimal("debit"));
        line.setCredit(rs.getBigDecimal("credit"));
        line.setLineNumber(rs.getInt("line_number"));
        return line;
    }
}

