package com.comptafx.entities;

import com.comptafx.metier.Auditable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents an invoice (both payable and receivable)
 */
public class Invoice implements Auditable {
    
    private Long id;
    private String invoiceNumber;
    private InvoiceType type;
    private Long vendorId;
    private Long customerId;
    private String vendorName;
    private String customerName;
    private LocalDate invoiceDate;
    private LocalDate dueDate;
    private LocalDate paymentDate;
    private InvoiceStatus status;
    private List<InvoiceLine> lines;
    private BigDecimal subtotal;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;
    private BigDecimal paidAmount;
    private String currency;
    private String notes;
    private Long clientId;
    
    // Audit fields
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    
    public Invoice() {
        this.lines = new ArrayList<>();
        this.status = InvoiceStatus.DRAFT;
        this.subtotal = BigDecimal.ZERO;
        this.taxAmount = BigDecimal.ZERO;
        this.totalAmount = BigDecimal.ZERO;
        this.paidAmount = BigDecimal.ZERO;
        this.currency = "EUR";
        this.invoiceDate = LocalDate.now();
    }
    
    public void addLine(InvoiceLine line) {
        line.setInvoice(this);
        this.lines.add(line);
        recalculateTotals();
    }
    
    public void removeLine(InvoiceLine line) {
        this.lines.remove(line);
        recalculateTotals();
    }
    
    public void recalculateTotals() {
        this.subtotal = lines.stream()
                .map(InvoiceLine::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        this.taxAmount = lines.stream()
                .map(InvoiceLine::getTaxAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        this.totalAmount = subtotal.add(taxAmount);
    }
    
    public BigDecimal getBalanceDue() {
        return totalAmount.subtract(paidAmount);
    }
    
    public boolean isOverdue() {
        return dueDate != null && LocalDate.now().isAfter(dueDate) 
                && status != InvoiceStatus.PAID && status != InvoiceStatus.CANCELLED;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }
    
    public InvoiceType getType() { return type; }
    public void setType(InvoiceType type) { this.type = type; }
    
    public Long getVendorId() { return vendorId; }
    public void setVendorId(Long vendorId) { this.vendorId = vendorId; }
    
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    
    public String getVendorName() { return vendorName; }
    public void setVendorName(String vendorName) { this.vendorName = vendorName; }
    
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    
    public LocalDate getInvoiceDate() { return invoiceDate; }
    public void setInvoiceDate(LocalDate invoiceDate) { this.invoiceDate = invoiceDate; }
    
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    
    public LocalDate getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDate paymentDate) { this.paymentDate = paymentDate; }
    
    public InvoiceStatus getStatus() { return status; }
    public void setStatus(InvoiceStatus status) { this.status = status; }
    
    public List<InvoiceLine> getLines() { return lines; }
    public void setLines(List<InvoiceLine> lines) { this.lines = lines; }
    
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    
    public BigDecimal getTaxAmount() { return taxAmount; }
    public void setTaxAmount(BigDecimal taxAmount) { this.taxAmount = taxAmount; }
    
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    
    public BigDecimal getPaidAmount() { return paidAmount; }
    public void setPaidAmount(BigDecimal paidAmount) { this.paidAmount = paidAmount; }
    
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public Long getClientId() { return clientId; }
    public void setClientId(Long clientId) { this.clientId = clientId; }
    
    @Override
    public LocalDateTime getCreatedAt() { return createdAt; }
    @Override
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    @Override
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    @Override
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    @Override
    public String getCreatedBy() { return createdBy; }
    @Override
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    
    @Override
    public String getUpdatedBy() { return updatedBy; }
    @Override
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Invoice invoice = (Invoice) o;
        return Objects.equals(id, invoice.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

