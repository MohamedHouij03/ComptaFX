package com.comptafx.entities;

import com.comptafx.metier.Auditable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a journal entry (accounting transaction)
 */
public class JournalEntry implements Auditable {
    
    private Long id;
    private String entryNumber;
    private LocalDate entryDate;
    private String description;
    private String reference;
    private JournalEntryStatus status;
    private List<JournalEntryLine> lines;
    private Long clientId;
    private boolean isRecurring;
    private String recurringFrequency;
    
    // Audit fields
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    
    public JournalEntry() {
        this.lines = new ArrayList<>();
        this.status = JournalEntryStatus.DRAFT;
        this.entryDate = LocalDate.now();
    }
    
    public void addLine(JournalEntryLine line) {
        line.setJournalEntry(this);
        this.lines.add(line);
    }
    
    public void removeLine(JournalEntryLine line) {
        this.lines.remove(line);
    }
    
    public BigDecimal getTotalDebit() {
        return lines.stream()
                .map(JournalEntryLine::getDebit)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    public BigDecimal getTotalCredit() {
        return lines.stream()
                .map(JournalEntryLine::getCredit)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    public boolean isBalanced() {
        return getTotalDebit().compareTo(getTotalCredit()) == 0;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getEntryNumber() { return entryNumber; }
    public void setEntryNumber(String entryNumber) { this.entryNumber = entryNumber; }
    
    public LocalDate getEntryDate() { return entryDate; }
    public void setEntryDate(LocalDate entryDate) { this.entryDate = entryDate; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }
    
    public JournalEntryStatus getStatus() { return status; }
    public void setStatus(JournalEntryStatus status) { this.status = status; }
    
    public List<JournalEntryLine> getLines() { return lines; }
    public void setLines(List<JournalEntryLine> lines) { this.lines = lines; }
    
    public Long getClientId() { return clientId; }
    public void setClientId(Long clientId) { this.clientId = clientId; }
    
    public boolean isRecurring() { return isRecurring; }
    public void setRecurring(boolean recurring) { isRecurring = recurring; }
    
    public String getRecurringFrequency() { return recurringFrequency; }
    public void setRecurringFrequency(String recurringFrequency) { this.recurringFrequency = recurringFrequency; }
    
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
        JournalEntry that = (JournalEntry) o;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

