package com.comptafx.dao;

import com.comptafx.entities.Invoice;
import com.comptafx.entities.InvoiceStatus;
import com.comptafx.entities.InvoiceType;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Interface for Invoice Data Access Object
 */
public interface IInvoiceDAO {
    
    Invoice save(Invoice invoice) throws DatabaseException;
    
    Optional<Invoice> findById(Long id) throws DatabaseException;
    
    Optional<Invoice> findByNumber(String invoiceNumber) throws DatabaseException;
    
    List<Invoice> findAll() throws DatabaseException;
    
    List<Invoice> findByStatus(InvoiceStatus status) throws DatabaseException;
    
    List<Invoice> findOverdue() throws DatabaseException;
    
    List<Invoice> findByCustomerId(Long customerId) throws DatabaseException;
    
    List<Invoice> findByVendorId(Long vendorId) throws DatabaseException;
    
    Map<InvoiceStatus, Long> getCountByStatus() throws DatabaseException;
    
    BigDecimal getTotalReceivables() throws DatabaseException;
    
    BigDecimal getTotalPayables() throws DatabaseException;
    
    List<Invoice> findDueSoon(int daysAhead) throws DatabaseException;
    
    void update(Invoice invoice) throws DatabaseException;
    
    void delete(Long id) throws DatabaseException;
    
    long count() throws DatabaseException;
    
    boolean existsById(Long id) throws DatabaseException;
    
    String generateInvoiceNumber(InvoiceType type) throws DatabaseException;
    
    void setCurrentClientId(Long clientId);
}

