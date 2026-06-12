package com.comptafx.metier;

import com.comptafx.entities.JournalEntry;
import com.comptafx.entities.JournalEntryLine;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Interface pour les opérations sur les écritures comptables
 */
public interface ServiceEcritures {
    
    JournalEntry createEntry(JournalEntry entry) throws ComptaException;
    
    void updateEntry(JournalEntry entry) throws ComptaException;
    
    JournalEntry getEntryById(Long id) throws ComptaException;
    
    List<JournalEntry> getAllEntries() throws ComptaException;
    
    List<JournalEntry> getEntriesByDateRange(LocalDate startDate, LocalDate endDate) throws ComptaException;
    
    List<JournalEntry> getEntriesByAccount(String accountCode) throws ComptaException;
    
    Map<String, BigDecimal> getTrialBalance() throws ComptaException;
    
    Map<String, List<JournalEntryLine>> getLedger() throws ComptaException;
    
    void validateEntry(JournalEntry entry) throws ComptaException;
    
    void postEntry(Long entryId) throws ComptaException;
    
    void reverseEntry(Long entryId) throws ComptaException;
    
    void deleteEntry(Long entryId) throws ComptaException;
    
    BigDecimal getTotalDebits(LocalDate startDate, LocalDate endDate) throws ComptaException;
    
    BigDecimal getTotalCredits(LocalDate startDate, LocalDate endDate) throws ComptaException;
}

