package com.comptafx.dao;

import com.comptafx.entities.*;

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
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Data Access Object for Invoice entities
 */
public class InvoiceDAO implements IInvoiceDAO {
    
    
    private Long currentClientId = 1L;
    
    public InvoiceDAO() {
        
    }
    
    public void setCurrentClientId(Long clientId) {
        this.currentClientId = clientId;
    }
    
    @Override
    public Invoice save(Invoice invoice) throws DatabaseException {
        String sql = """
            INSERT INTO invoices (invoice_number, type, vendor_id, customer_id, vendor_name, customer_name,
                                  invoice_date, due_date, status, subtotal, tax_amount, total_amount, 
                                  paid_amount, currency, notes, client_id, created_at, created_by)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;
        
        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, invoice.getInvoiceNumber());
                pstmt.setString(2, invoice.getType().name());
                pstmt.setObject(3, invoice.getVendorId());
                pstmt.setObject(4, invoice.getCustomerId());
                pstmt.setString(5, invoice.getVendorName());
                pstmt.setString(6, invoice.getCustomerName());
                pstmt.setDate(7, Date.valueOf(invoice.getInvoiceDate()));
                pstmt.setDate(8, invoice.getDueDate() != null ? Date.valueOf(invoice.getDueDate()) : null);
                pstmt.setString(9, invoice.getStatus().name());
                pstmt.setBigDecimal(10, invoice.getSubtotal());
                pstmt.setBigDecimal(11, invoice.getTaxAmount());
                pstmt.setBigDecimal(12, invoice.getTotalAmount());
                pstmt.setBigDecimal(13, invoice.getPaidAmount());
                pstmt.setString(14, invoice.getCurrency());
                pstmt.setString(15, invoice.getNotes());
                pstmt.setLong(16, currentClientId);
                pstmt.setTimestamp(17, Timestamp.valueOf(LocalDateTime.now()));
                pstmt.setString(18, invoice.getCreatedBy());
                
                pstmt.executeUpdate();
                
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        invoice.setId(rs.getLong(1));
                    }
                }
            }
            
            saveLines(conn, invoice);
            conn.commit();
            return invoice;
            
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            throw new DatabaseException("Failed to save invoice", e);
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }
    
    private void saveLines(Connection conn, Invoice invoice) throws SQLException {
        String sql = """
            INSERT INTO invoice_lines (invoice_id, description, quantity, unit_price, tax_rate, 
                                       account_code, line_number)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            int lineNumber = 1;
            for (InvoiceLine line : invoice.getLines()) {
                pstmt.setLong(1, invoice.getId());
                pstmt.setString(2, line.getDescription());
                pstmt.setBigDecimal(3, line.getQuantity());
                pstmt.setBigDecimal(4, line.getUnitPrice());
                pstmt.setBigDecimal(5, line.getTaxRate());
                pstmt.setString(6, line.getAccountCode());
                pstmt.setInt(7, lineNumber++);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
    }
    
    @Override
    public Optional<Invoice> findById(Long id) throws DatabaseException {
        String sql = "SELECT * FROM invoices WHERE id = ?";
        Invoice invoice = null;
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    invoice = mapResultSetToInvoice(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find invoice", e);
        }
        
        if (invoice != null) {
            invoice.setLines(findLinesByInvoiceId(invoice.getId()));
            return Optional.of(invoice);
        }
        return Optional.empty();
    }
    
    public Optional<Invoice> findByNumber(String invoiceNumber) throws DatabaseException {
        String sql = "SELECT * FROM invoices WHERE invoice_number = ? AND client_id = ?";
        Invoice invoice = null;
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, invoiceNumber);
            pstmt.setLong(2, currentClientId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    invoice = mapResultSetToInvoice(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find invoice by number", e);
        }
        
        if (invoice != null) {
            invoice.setLines(findLinesByInvoiceId(invoice.getId()));
            return Optional.of(invoice);
        }
        return Optional.empty();
    }
    
    private List<InvoiceLine> findLinesByInvoiceId(Long invoiceId) throws DatabaseException {
        String sql = "SELECT * FROM invoice_lines WHERE invoice_id = ? ORDER BY line_number";
        List<InvoiceLine> lines = new ArrayList<>();
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, invoiceId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    lines.add(mapResultSetToLine(rs));
                }
            }
            
            return lines;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find invoice lines", e);
        }
    }
    
    @Override
    public List<Invoice> findAll() throws DatabaseException {
        String sql = "SELECT * FROM invoices ORDER BY invoice_date DESC";
        List<Invoice> invoices = new ArrayList<>();
        
        // Get fresh connection each time
        Connection conn = DatabaseConfig.getConnection();
        if (conn == null) {
            throw new DatabaseException("Database connection is null");
        }
        
        // First, load all invoices without lines
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Invoice invoice = mapResultSetToInvoice(rs);
                invoices.add(invoice);
            }
        } catch (SQLException e) {
            System.err.println("Error loading invoices: " + e.getMessage());
            e.printStackTrace();
            throw new DatabaseException("Failed to retrieve invoices", e);
        }
        
        // Then, load lines for each invoice (separate queries)
        for (Invoice invoice : invoices) {
            invoice.setLines(findLinesByInvoiceId(invoice.getId()));
        }
        
        System.out.println("Found " + invoices.size() + " invoices in database");
        return invoices;
    }
    
    /**
     * Find invoices by status using Stream
     */
    public List<Invoice> findByStatus(InvoiceStatus status) throws DatabaseException {
        return findAll().stream()
                .filter(invoice -> invoice.getStatus() == status)
                .collect(Collectors.toList());
    }
    
    /**
     * Find overdue invoices using Stream and Lambda
     */
    public List<Invoice> findOverdue() throws DatabaseException {
        LocalDate today = LocalDate.now();
        return findAll().stream()
                .filter(invoice -> invoice.getDueDate() != null &&
                        invoice.getDueDate().isBefore(today) &&
                        invoice.getStatus() != InvoiceStatus.PAID &&
                        invoice.getStatus() != InvoiceStatus.CANCELLED)
                .sorted(Comparator.comparing(Invoice::getDueDate))
                .collect(Collectors.toList());
    }
    
    /**
     * Find invoices by customer using Stream
     */
    public List<Invoice> findByCustomerId(Long customerId) throws DatabaseException {
        return findAll().stream()
                .filter(invoice -> customerId.equals(invoice.getCustomerId()))
                .collect(Collectors.toList());
    }
    
    /**
     * Find invoices by vendor using Stream
     */
    public List<Invoice> findByVendorId(Long vendorId) throws DatabaseException {
        return findAll().stream()
                .filter(invoice -> vendorId.equals(invoice.getVendorId()))
                .collect(Collectors.toList());
    }
    
    /**
     * Get invoice count by status as Map
     */
    public Map<InvoiceStatus, Long> getCountByStatus() throws DatabaseException {
        return findAll().stream()
                .collect(Collectors.groupingBy(
                        Invoice::getStatus,
                        () -> new EnumMap<>(InvoiceStatus.class),
                        Collectors.counting()
                ));
    }
    
    /**
     * Calculate total receivables
     */
    public BigDecimal getTotalReceivables() throws DatabaseException {
        return findAll().stream()
                .filter(invoice -> invoice.getType() == InvoiceType.RECEIVABLE)
                .filter(invoice -> invoice.getStatus() != InvoiceStatus.PAID &&
                        invoice.getStatus() != InvoiceStatus.CANCELLED)
                .map(Invoice::getBalanceDue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    /**
     * Calculate total payables
     */
    public BigDecimal getTotalPayables() throws DatabaseException {
        return findAll().stream()
                .filter(invoice -> invoice.getType() == InvoiceType.PAYABLE)
                .filter(invoice -> invoice.getStatus() != InvoiceStatus.PAID &&
                        invoice.getStatus() != InvoiceStatus.CANCELLED)
                .map(Invoice::getBalanceDue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    /**
     * Find invoices due soon
     */
    public List<Invoice> findDueSoon(int daysAhead) throws DatabaseException {
        LocalDate today = LocalDate.now();
        LocalDate futureDate = today.plusDays(daysAhead);
        
        return findAll().stream()
                .filter(invoice -> invoice.getDueDate() != null &&
                        !invoice.getDueDate().isBefore(today) &&
                        !invoice.getDueDate().isAfter(futureDate) &&
                        invoice.getStatus() != InvoiceStatus.PAID &&
                        invoice.getStatus() != InvoiceStatus.CANCELLED)
                .sorted(Comparator.comparing(Invoice::getDueDate))
                .collect(Collectors.toList());
    }
    
    @Override
    public void update(Invoice invoice) throws DatabaseException {
        String sql = """
            UPDATE invoices 
            SET type = ?, vendor_id = ?, customer_id = ?, vendor_name = ?, customer_name = ?,
                invoice_date = ?, due_date = ?, payment_date = ?, status = ?, subtotal = ?, 
                tax_amount = ?, total_amount = ?, paid_amount = ?, currency = ?, notes = ?,
                updated_at = ?, updated_by = ?
            WHERE id = ?
        """;
        
        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, invoice.getType().name());
                pstmt.setObject(2, invoice.getVendorId());
                pstmt.setObject(3, invoice.getCustomerId());
                pstmt.setString(4, invoice.getVendorName());
                pstmt.setString(5, invoice.getCustomerName());
                pstmt.setDate(6, Date.valueOf(invoice.getInvoiceDate()));
                pstmt.setDate(7, invoice.getDueDate() != null ? Date.valueOf(invoice.getDueDate()) : null);
                pstmt.setDate(8, invoice.getPaymentDate() != null ? Date.valueOf(invoice.getPaymentDate()) : null);
                pstmt.setString(9, invoice.getStatus().name());
                pstmt.setBigDecimal(10, invoice.getSubtotal());
                pstmt.setBigDecimal(11, invoice.getTaxAmount());
                pstmt.setBigDecimal(12, invoice.getTotalAmount());
                pstmt.setBigDecimal(13, invoice.getPaidAmount());
                pstmt.setString(14, invoice.getCurrency());
                pstmt.setString(15, invoice.getNotes());
                pstmt.setTimestamp(16, Timestamp.valueOf(LocalDateTime.now()));
                pstmt.setString(17, invoice.getUpdatedBy());
                pstmt.setLong(18, invoice.getId());
                
                pstmt.executeUpdate();
            }
            
            deleteLinesByInvoiceId(conn, invoice.getId());
            saveLines(conn, invoice);
            
            conn.commit();
            
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            throw new DatabaseException("Failed to update invoice", e);
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }
    
    private void deleteLinesByInvoiceId(Connection conn, Long invoiceId) throws SQLException {
        String sql = "DELETE FROM invoice_lines WHERE invoice_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, invoiceId);
            pstmt.executeUpdate();
        }
    }
    
    @Override
    public void delete(Long id) throws DatabaseException {
        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);
            
            // Supprimer d'abord les lignes de facture
            deleteLinesByInvoiceId(conn, id);
            
            // Puis supprimer la facture
            String sql = "DELETE FROM invoices WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setLong(1, id);
                int rowsAffected = pstmt.executeUpdate();
                
                if (rowsAffected == 0) {
                    throw new DatabaseException("Aucune facture trouvée avec l'ID: " + id);
                }
            }
            
            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            throw new DatabaseException("Erreur lors de la suppression de la facture", e);
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }
    
    @Override
    public long count() throws DatabaseException {
        String sql = "SELECT COUNT(*) FROM invoices WHERE client_id = ?";
        
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
            throw new DatabaseException("Failed to count invoices", e);
        }
    }
    
    @Override
    public boolean existsById(Long id) throws DatabaseException {
        return findById(id).isPresent();
    }
    
    public String generateInvoiceNumber(InvoiceType type) throws DatabaseException {
        String prefix = type == InvoiceType.RECEIVABLE ? "INV" : "BILL";
        prefix += "-" + LocalDate.now().getYear() + "-";
        long count = count() + 1;
        return String.format("%s%05d", prefix, count);
    }
    
    private Invoice mapResultSetToInvoice(ResultSet rs) throws SQLException {
        Invoice invoice = new Invoice();
        invoice.setId(rs.getLong("id"));
        invoice.setInvoiceNumber(rs.getString("invoice_number"));
        invoice.setType(InvoiceType.valueOf(rs.getString("type")));
        
        long vendorId = rs.getLong("vendor_id");
        if (!rs.wasNull()) invoice.setVendorId(vendorId);
        
        long customerId = rs.getLong("customer_id");
        if (!rs.wasNull()) invoice.setCustomerId(customerId);
        
        invoice.setVendorName(rs.getString("vendor_name"));
        invoice.setCustomerName(rs.getString("customer_name"));
        invoice.setInvoiceDate(rs.getDate("invoice_date").toLocalDate());
        
        Date dueDate = rs.getDate("due_date");
        if (dueDate != null) invoice.setDueDate(dueDate.toLocalDate());
        
        Date paymentDate = rs.getDate("payment_date");
        if (paymentDate != null) invoice.setPaymentDate(paymentDate.toLocalDate());
        
        invoice.setStatus(InvoiceStatus.valueOf(rs.getString("status")));
        invoice.setSubtotal(rs.getBigDecimal("subtotal"));
        invoice.setTaxAmount(rs.getBigDecimal("tax_amount"));
        invoice.setTotalAmount(rs.getBigDecimal("total_amount"));
        invoice.setPaidAmount(rs.getBigDecimal("paid_amount"));
        invoice.setCurrency(rs.getString("currency"));
        invoice.setNotes(rs.getString("notes"));
        invoice.setClientId(rs.getLong("client_id"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) invoice.setCreatedAt(createdAt.toLocalDateTime());
        
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) invoice.setUpdatedAt(updatedAt.toLocalDateTime());
        
        invoice.setCreatedBy(rs.getString("created_by"));
        invoice.setUpdatedBy(rs.getString("updated_by"));
        
        return invoice;
    }
    
    private InvoiceLine mapResultSetToLine(ResultSet rs) throws SQLException {
        InvoiceLine line = new InvoiceLine();
        line.setId(rs.getLong("id"));
        line.setDescription(rs.getString("description"));
        line.setQuantity(rs.getBigDecimal("quantity"));
        line.setUnitPrice(rs.getBigDecimal("unit_price"));
        line.setTaxRate(rs.getBigDecimal("tax_rate"));
        line.setAccountCode(rs.getString("account_code"));
        line.setLineNumber(rs.getInt("line_number"));
        return line;
    }
}

