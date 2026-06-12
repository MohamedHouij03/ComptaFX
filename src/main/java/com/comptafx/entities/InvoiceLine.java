package com.comptafx.entities;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Represents a line item in an invoice
 */
public class InvoiceLine {
    
    private Long id;
    private Invoice invoice;
    private String description;
    private BigDecimal quantity;
    private BigDecimal unitPrice;
    private BigDecimal taxRate;
    private String accountCode;
    private int lineNumber;
    
    public InvoiceLine() {
        this.quantity = BigDecimal.ONE;
        this.unitPrice = BigDecimal.ZERO;
        this.taxRate = BigDecimal.ZERO;
    }
    
    public InvoiceLine(String description, BigDecimal quantity, BigDecimal unitPrice, BigDecimal taxRate) {
        this.description = description;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.taxRate = taxRate;
    }
    
    public BigDecimal getLineTotal() {
        return quantity.multiply(unitPrice).setScale(2, RoundingMode.HALF_UP);
    }
    
    public BigDecimal getTaxAmount() {
        return getLineTotal().multiply(taxRate).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }
    
    public BigDecimal getLineTotalWithTax() {
        return getLineTotal().add(getTaxAmount());
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Invoice getInvoice() { return invoice; }
    public void setInvoice(Invoice invoice) { this.invoice = invoice; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }
    
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
    
    public BigDecimal getTaxRate() { return taxRate; }
    public void setTaxRate(BigDecimal taxRate) { this.taxRate = taxRate; }
    
    public String getAccountCode() { return accountCode; }
    public void setAccountCode(String accountCode) { this.accountCode = accountCode; }
    
    public int getLineNumber() { return lineNumber; }
    public void setLineNumber(int lineNumber) { this.lineNumber = lineNumber; }
}

