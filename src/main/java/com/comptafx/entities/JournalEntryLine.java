package com.comptafx.entities;

import java.math.BigDecimal;
import java.util.Objects;


public class JournalEntryLine {
    
    private Long id;
    private JournalEntry journalEntry;
    private String accountCode;
    private String accountName;
    private String description;
    private BigDecimal debit;
    private BigDecimal credit;
    private int lineNumber;
    
    public JournalEntryLine() {
        this.debit = BigDecimal.ZERO;
        this.credit = BigDecimal.ZERO;
    }
    
    public JournalEntryLine(String accountCode, BigDecimal debit, BigDecimal credit) {
        this();
        this.accountCode = accountCode;
        this.debit = debit != null ? debit : BigDecimal.ZERO;
        this.credit = credit != null ? credit : BigDecimal.ZERO;
    }
    
    public static JournalEntryLine debit(String accountCode, BigDecimal amount) {
        return new JournalEntryLine(accountCode, amount, BigDecimal.ZERO);
    }
    
    public static JournalEntryLine credit(String accountCode, BigDecimal amount) {
        return new JournalEntryLine(accountCode, BigDecimal.ZERO, amount);
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public JournalEntry getJournalEntry() { return journalEntry; }
    public void setJournalEntry(JournalEntry journalEntry) { this.journalEntry = journalEntry; }
    
    public String getAccountCode() { return accountCode; }
    public void setAccountCode(String accountCode) { this.accountCode = accountCode; }
    
    public String getAccountName() { return accountName; }
    public void setAccountName(String accountName) { this.accountName = accountName; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public BigDecimal getDebit() { return debit; }
    public void setDebit(BigDecimal debit) { this.debit = debit; }
    
    public BigDecimal getCredit() { return credit; }
    public void setCredit(BigDecimal credit) { this.credit = credit; }
    
    public int getLineNumber() { return lineNumber; }
    public void setLineNumber(int lineNumber) { this.lineNumber = lineNumber; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JournalEntryLine that = (JournalEntryLine) o;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

