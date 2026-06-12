package com.comptafx.dao;

import com.comptafx.entities.JournalEntry;
import com.comptafx.entities.JournalEntryLine;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Interface for JournalEntry Data Access Object
 */
public interface IJournalEntryDAO {
    
    JournalEntry save(JournalEntry entry) throws DatabaseException;
    
    Optional<JournalEntry> findById(Long id) throws DatabaseException;
    
    List<JournalEntry> findAll() throws DatabaseException;
    
    List<JournalEntry> findByDateRange(LocalDate startDate, LocalDate endDate) throws DatabaseException;
    
    List<JournalEntry> findByAccountCode(String accountCode) throws DatabaseException;
    
    Map<String, BigDecimal> getTrialBalance() throws DatabaseException;
    
    Map<String, List<JournalEntryLine>> getLedger() throws DatabaseException;
    
    BigDecimal getTotalDebits(LocalDate startDate, LocalDate endDate) throws DatabaseException;
    
    BigDecimal getTotalCredits(LocalDate startDate, LocalDate endDate) throws DatabaseException;
    
    void update(JournalEntry entry) throws DatabaseException;
    
    void delete(Long id) throws DatabaseException;
    
    long count() throws DatabaseException;
    
    boolean existsById(Long id) throws DatabaseException;
    
    String generateEntryNumber() throws DatabaseException;
    
    void setCurrentClientId(Long clientId);
}

