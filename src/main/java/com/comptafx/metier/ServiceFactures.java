package com.comptafx.metier;

import com.comptafx.entities.Invoice;
import com.comptafx.entities.InvoiceStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Interface pour les opérations de gestion des factures
 */
public interface ServiceFactures {
    
    Invoice createInvoice(Invoice invoice) throws ComptaException;
    
    Invoice getInvoiceById(Long id) throws ComptaException;
    
    Invoice getInvoiceByNumber(String invoiceNumber) throws ComptaException;
    
    List<Invoice> getAllInvoices() throws ComptaException;
    
    List<Invoice> getInvoicesByClient(Long clientId) throws ComptaException;
    
    List<Invoice> getInvoicesByVendor(Long vendorId) throws ComptaException;
    
    List<Invoice> getInvoicesByStatus(InvoiceStatus status) throws ComptaException;
    
    List<Invoice> getOverdueInvoices() throws ComptaException;
    
    Map<InvoiceStatus, Long> getInvoiceCountByStatus() throws ComptaException;
    
    BigDecimal getTotalReceivables() throws ComptaException;
    
    BigDecimal getTotalPayables() throws ComptaException;
    
    void updateInvoice(Invoice invoice) throws ComptaException;
    
    void markAsPaid(Long invoiceId, LocalDate paymentDate) throws ComptaException;
    
    void cancelInvoice(Long invoiceId) throws ComptaException;
    
    List<Invoice> getInvoicesDueSoon(int daysAhead) throws ComptaException;
}


