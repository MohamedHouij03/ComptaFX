package com.comptafx.metier;

import com.comptafx.dao.InvoiceDAO;
import com.comptafx.entities.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Exception thrown when validation fails
 */
class ValidationException extends ComptaException {
    private String fieldName;

    public ValidationException(String message) {
        super(message, "VALIDATION_ERROR");
    }

    public ValidationException(String message, String fieldName) {
        super(message, "VALIDATION_ERROR");
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }
}

/**
 * Implémentation de l'interface ServiceFactures
 */
public class ServiceFacturesImpl implements ServiceFactures {
    
    private final InvoiceDAO invoiceDAO;
    
    public ServiceFacturesImpl() {
        this.invoiceDAO = new InvoiceDAO();
    }
    
    @Override
    public Invoice createInvoice(Invoice invoice) throws ComptaException {
        validateInvoice(invoice);
        
        if (invoice.getInvoiceNumber() == null || invoice.getInvoiceNumber().isEmpty()) {
            invoice.setInvoiceNumber(invoiceDAO.generateInvoiceNumber(invoice.getType()));
        }
        
        invoice.recalculateTotals();
        return invoiceDAO.save(invoice);
    }
    
    @Override
    public Invoice getInvoiceById(Long id) throws ComptaException {
        return invoiceDAO.findById(id)
                .orElseThrow(() -> new ComptaException("Invoice not found: " + id));
    }
    
    @Override
    public Invoice getInvoiceByNumber(String invoiceNumber) throws ComptaException {
        return invoiceDAO.findByNumber(invoiceNumber)
                .orElseThrow(() -> new ComptaException("Invoice not found: " + invoiceNumber));
    }
    
    @Override
    public List<Invoice> getAllInvoices() throws ComptaException {
        return invoiceDAO.findAll();
    }
    
    @Override
    public List<Invoice> getInvoicesByClient(Long clientId) throws ComptaException {
        return getAllInvoices().stream()
                .filter(inv -> clientId.equals(inv.getCustomerId()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Invoice> getInvoicesByVendor(Long vendorId) throws ComptaException {
        return getAllInvoices().stream()
                .filter(inv -> vendorId.equals(inv.getVendorId()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Invoice> getInvoicesByStatus(InvoiceStatus status) throws ComptaException {
        return getAllInvoices().stream()
                .filter(inv -> inv.getStatus() == status)
                .sorted(Comparator.comparing(Invoice::getInvoiceDate).reversed())
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Invoice> getOverdueInvoices() throws ComptaException {
        LocalDate today = LocalDate.now();
        return getAllInvoices().stream()
                .filter(inv -> inv.getDueDate() != null)
                .filter(inv -> inv.getDueDate().isBefore(today))
                .filter(inv -> inv.getStatus() != InvoiceStatus.PAID)
                .filter(inv -> inv.getStatus() != InvoiceStatus.CANCELLED)
                .sorted(Comparator.comparing(Invoice::getDueDate))
                .collect(Collectors.toList());
    }
    
    @Override
    public Map<InvoiceStatus, Long> getInvoiceCountByStatus() throws ComptaException {
        return getAllInvoices().stream()
                .collect(Collectors.groupingBy(
                        Invoice::getStatus,
                        () -> new EnumMap<>(InvoiceStatus.class),
                        Collectors.counting()
                ));
    }
    
    @Override
    public BigDecimal getTotalReceivables() throws ComptaException {
        return getAllInvoices().stream()
                .filter(inv -> inv.getType() == InvoiceType.RECEIVABLE)
                .filter(inv -> inv.getStatus() != InvoiceStatus.PAID)
                .filter(inv -> inv.getStatus() != InvoiceStatus.CANCELLED)
                .map(Invoice::getBalanceDue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    @Override
    public BigDecimal getTotalPayables() throws ComptaException {
        return getAllInvoices().stream()
                .filter(inv -> inv.getType() == InvoiceType.PAYABLE)
                .filter(inv -> inv.getStatus() != InvoiceStatus.PAID)
                .filter(inv -> inv.getStatus() != InvoiceStatus.CANCELLED)
                .map(Invoice::getBalanceDue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    @Override
    public void updateInvoice(Invoice invoice) throws ComptaException {
        validateInvoice(invoice);
        invoice.recalculateTotals();
        invoiceDAO.update(invoice);
    }
    
    @Override
    public void markAsPaid(Long invoiceId, LocalDate paymentDate) throws ComptaException {
        Invoice invoice = getInvoiceById(invoiceId);
        invoice.setStatus(InvoiceStatus.PAID);
        invoice.setPaymentDate(paymentDate);
        invoice.setPaidAmount(invoice.getTotalAmount());
        invoiceDAO.update(invoice);
    }
    
    @Override
    public void cancelInvoice(Long invoiceId) throws ComptaException {
        System.out.println("ServiceFactures.cancelInvoice appelé avec ID: " + invoiceId);
        Invoice invoice = getInvoiceById(invoiceId);
        System.out.println("Found invoice: " + invoice.getInvoiceNumber() + " Status: " + invoice.getStatus());
        
        if (invoice.getStatus() == InvoiceStatus.PAID) {
            throw new ValidationException("Cannot cancel paid invoice");
        }
        invoice.setStatus(InvoiceStatus.CANCELLED);
        System.out.println("Updating invoice status to CANCELLED");
        invoiceDAO.update(invoice);
        System.out.println("Invoice updated successfully");
    }
    
    public void deleteInvoice(Long invoiceId) throws ComptaException {
        Invoice invoice = getInvoiceById(invoiceId);
        if (invoice.getStatus() == InvoiceStatus.PAID) {
            throw new ValidationException("Cannot delete paid invoice. Cancel it first.");
        }
        invoiceDAO.delete(invoiceId);
    }
    
    @Override
    public List<Invoice> getInvoicesDueSoon(int daysAhead) throws ComptaException {
        LocalDate today = LocalDate.now();
        LocalDate futureDate = today.plusDays(daysAhead);
        
        return getAllInvoices().stream()
                .filter(inv -> inv.getDueDate() != null)
                .filter(inv -> !inv.getDueDate().isBefore(today))
                .filter(inv -> !inv.getDueDate().isAfter(futureDate))
                .filter(inv -> inv.getStatus() != InvoiceStatus.PAID)
                .filter(inv -> inv.getStatus() != InvoiceStatus.CANCELLED)
                .sorted(Comparator.comparing(Invoice::getDueDate))
                .collect(Collectors.toList());
    }
    
    private void validateInvoice(Invoice invoice) throws ValidationException {
        List<String> errors = new ArrayList<>();
        
        if (invoice.getType() == null) {
            errors.add("Invoice type is required");
        }
        if (invoice.getInvoiceDate() == null) {
            errors.add("Invoice date is required");
        }
        
        if (!errors.isEmpty()) {
            throw new ValidationException(String.join("; ", errors));
        }
    }
    
    /**
     * Get aging report using Stream operations
     */
    public Map<String, BigDecimal> getAgingReport() throws ComptaException {
        LocalDate today = LocalDate.now();
        
        Map<String, BigDecimal> aging = new LinkedHashMap<>();
        aging.put("Current", BigDecimal.ZERO);
        aging.put("1-30 Days", BigDecimal.ZERO);
        aging.put("31-60 Days", BigDecimal.ZERO);
        aging.put("61-90 Days", BigDecimal.ZERO);
        aging.put("Over 90 Days", BigDecimal.ZERO);
        
        getAllInvoices().stream()
                .filter(inv -> inv.getType() == InvoiceType.RECEIVABLE)
                .filter(inv -> inv.getStatus() != InvoiceStatus.PAID)
                .filter(inv -> inv.getStatus() != InvoiceStatus.CANCELLED)
                .forEach(inv -> {
                    long daysOverdue = java.time.temporal.ChronoUnit.DAYS.between(
                            inv.getDueDate() != null ? inv.getDueDate() : inv.getInvoiceDate(),
                            today
                    );
                    
                    String bucket;
                    if (daysOverdue <= 0) bucket = "Current";
                    else if (daysOverdue <= 30) bucket = "1-30 Days";
                    else if (daysOverdue <= 60) bucket = "31-60 Days";
                    else if (daysOverdue <= 90) bucket = "61-90 Days";
                    else bucket = "Over 90 Days";
                    
                    aging.merge(bucket, inv.getBalanceDue(), BigDecimal::add);
                });
        
        return aging;
    }
}

