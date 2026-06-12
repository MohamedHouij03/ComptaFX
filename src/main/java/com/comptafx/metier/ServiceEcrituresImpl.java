package com.comptafx.metier;

import com.comptafx.dao.JournalEntryDAO;
import com.comptafx.entities.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;


class UnbalancedEntryException extends ComptaException {
    private BigDecimal totalDebit;
    private BigDecimal totalCredit;
    
    public UnbalancedEntryException(BigDecimal totalDebit, BigDecimal totalCredit) {
        super(String.format("Unbalanced entry: Debit=%.2f, Credit=%.2f", 
                totalDebit, totalCredit), "UNBALANCED_ENTRY");
        this.totalDebit = totalDebit;
        this.totalCredit = totalCredit;
    }
    
    public BigDecimal getTotalDebit() {
        return totalDebit;
    }
    
    public BigDecimal getTotalCredit() {
        return totalCredit;
    }
}


public class ServiceEcrituresImpl implements ServiceEcritures {
    
    private final JournalEntryDAO journalDAO;
    
    public ServiceEcrituresImpl() {
        this.journalDAO = new JournalEntryDAO();
    }
    
    @Override
    public JournalEntry createEntry(JournalEntry entry) throws ComptaException {
        // Validate the entry
        validateEntry(entry);
        
        // Generate entry number if not provided
        if (entry.getEntryNumber() == null || entry.getEntryNumber().isEmpty()) {
            entry.setEntryNumber(journalDAO.generateEntryNumber());
        }
        
        return journalDAO.save(entry);
    }
    
    @Override
    public void updateEntry(JournalEntry entry) throws ComptaException {
        if (entry.getId() == null) {
            throw new ValidationException("Cannot update entry without ID");
        }
        
        // Validate the entry
        validateEntry(entry);
        
        journalDAO.update(entry);
    }
    
    @Override
    public JournalEntry getEntryById(Long id) throws ComptaException {
        return journalDAO.findById(id)
                .orElseThrow(() -> new ComptaException("Journal entry not found: " + id));
    }
    
    @Override
    public List<JournalEntry> getAllEntries() throws ComptaException {
        return journalDAO.findAll();
    }
    
    @Override
    public List<JournalEntry> getEntriesByDateRange(LocalDate startDate, LocalDate endDate) throws ComptaException {
        // Using Stream with Lambda for filtering
        return getAllEntries().stream()
                .filter(entry -> !entry.getEntryDate().isBefore(startDate))
                .filter(entry -> !entry.getEntryDate().isAfter(endDate))
                .sorted(Comparator.comparing(JournalEntry::getEntryDate)
                        .thenComparing(JournalEntry::getEntryNumber))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<JournalEntry> getEntriesByAccount(String accountCode) throws ComptaException {
        // Using Stream with nested Lambda for filtering by account
        return getAllEntries().stream()
                .filter(entry -> entry.getLines().stream()
                        .anyMatch(line -> accountCode.equals(line.getAccountCode())))
                .collect(Collectors.toList());
    }
    
    @Override
    public Map<String, BigDecimal> getTrialBalance() throws ComptaException {
        List<JournalEntry> postedEntries = getAllEntries().stream()
                .filter(entry -> entry.getStatus() == JournalEntryStatus.POSTED)
                .collect(Collectors.toList());
        
        // Using Stream to flatten and group by account
        Map<String, BigDecimal> trialBalance = postedEntries.stream()
                .flatMap(entry -> entry.getLines().stream())
                .collect(Collectors.groupingBy(
                        JournalEntryLine::getAccountCode,
                        LinkedHashMap::new,
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                line -> line.getDebit().subtract(line.getCredit()),
                                BigDecimal::add
                        )
                ));
        
        // Sort by account code
        return trialBalance.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }
    
    @Override
    public Map<String, List<JournalEntryLine>> getLedger() throws ComptaException {
        // Group all lines by account code using Stream
        return getAllEntries().stream()
                .filter(entry -> entry.getStatus() == JournalEntryStatus.POSTED)
                .flatMap(entry -> {
                    // Enrich lines with entry date for sorting
                    LocalDate entryDate = entry.getEntryDate();
                    return entry.getLines().stream()
                            .peek(line -> line.setDescription(
                                    entryDate + " - " + entry.getDescription()));
                })
                .collect(Collectors.groupingBy(
                        JournalEntryLine::getAccountCode,
                        TreeMap::new,
                        Collectors.toList()
                ));
    }
    
    @Override
    public void validateEntry(JournalEntry entry) throws ComptaException {
        // Using List to collect validation errors
        List<String> errors = new ArrayList<>();
        
        if (entry.getEntryDate() == null) {
            errors.add("Entry date is required");
        }
        
        if (entry.getLines() == null || entry.getLines().isEmpty()) {
            errors.add("Journal entry must have at least one line");
        } else {
            // Validate each line using Stream
            entry.getLines().stream()
                    .filter(line -> line.getAccountCode() == null || line.getAccountCode().isEmpty())
                    .findAny()
                    .ifPresent(line -> errors.add("All lines must have an account code"));
            
            // Check if entry is balanced
            if (!entry.isBalanced()) {
                throw new UnbalancedEntryException(entry.getTotalDebit(), entry.getTotalCredit());
            }
        }
        
        if (!errors.isEmpty()) {
            throw new ValidationException(String.join("; ", errors));
        }
    }
    
    @Override
    public void postEntry(Long entryId) throws ComptaException {
        JournalEntry entry = getEntryById(entryId);
        
        if (entry.getStatus() == JournalEntryStatus.POSTED) {
            throw new ValidationException("Entry is already posted");
        }
        
        if (entry.getStatus() == JournalEntryStatus.CANCELLED) {
            throw new ValidationException("Cannot post cancelled entry");
        }
        
        validateEntry(entry);
        
        entry.setStatus(JournalEntryStatus.POSTED);
        journalDAO.update(entry);
        
        // Note: Account balance updates removed - Account module deleted
    }
    
    @Override
    public void reverseEntry(Long entryId) throws ComptaException {
        JournalEntry originalEntry = getEntryById(entryId);
        
        if (originalEntry.getStatus() != JournalEntryStatus.POSTED) {
            throw new ValidationException("Can only reverse posted entries");
        }
        
        // Create reversal entry using Stream to swap debits and credits
        JournalEntry reversalEntry = new JournalEntry();
        reversalEntry.setEntryDate(LocalDate.now());
        reversalEntry.setDescription("Reversal of " + originalEntry.getEntryNumber());
        reversalEntry.setReference(originalEntry.getEntryNumber());
        
        // Reverse lines using Stream and Lambda
        List<JournalEntryLine> reversedLines = originalEntry.getLines().stream()
                .map(line -> {
                    JournalEntryLine reversedLine = new JournalEntryLine();
                    reversedLine.setAccountCode(line.getAccountCode());
                    reversedLine.setAccountName(line.getAccountName());
                    reversedLine.setDebit(line.getCredit()); // Swap debit and credit
                    reversedLine.setCredit(line.getDebit());
                    reversedLine.setDescription("Reversal");
                    return reversedLine;
                })
                .collect(Collectors.toList());
        
        reversedLines.forEach(reversalEntry::addLine);
        
        // Save and post the reversal
        JournalEntry savedReversal = createEntry(reversalEntry);
        postEntry(savedReversal.getId());
        
        // Mark original as reversed
        originalEntry.setStatus(JournalEntryStatus.REVERSED);
        journalDAO.update(originalEntry);
    }
    
    @Override
    public void deleteEntry(Long entryId) throws ComptaException {
        // Verify entry exists
        getEntryById(entryId);
        
        // Delete entry (all statuses allowed)
        journalDAO.delete(entryId);
    }
    
    @Override
    public BigDecimal getTotalDebits(LocalDate startDate, LocalDate endDate) throws ComptaException {
        return getEntriesByDateRange(startDate, endDate).stream()
                .filter(entry -> entry.getStatus() == JournalEntryStatus.POSTED)
                .flatMap(entry -> entry.getLines().stream())
                .map(JournalEntryLine::getDebit)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    @Override
    public BigDecimal getTotalCredits(LocalDate startDate, LocalDate endDate) throws ComptaException {
        return getEntriesByDateRange(startDate, endDate).stream()
                .filter(entry -> entry.getStatus() == JournalEntryStatus.POSTED)
                .flatMap(entry -> entry.getLines().stream())
                .map(JournalEntryLine::getCredit)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    /**
     * Get entry statistics using Stream operations
     */
    public Map<String, Object> getEntryStatistics() throws ComptaException {
        List<JournalEntry> entries = getAllEntries();
        
        Map<String, Object> stats = new LinkedHashMap<>();
        
        // Count by status using Stream and groupingBy
        Map<JournalEntryStatus, Long> countByStatus = entries.stream()
                .collect(Collectors.groupingBy(
                        JournalEntry::getStatus,
                        () -> new EnumMap<>(JournalEntryStatus.class),
                        Collectors.counting()
                ));
        stats.put("countByStatus", countByStatus);
        
        // Entries by month using Stream
        Map<String, Long> entriesByMonth = entries.stream()
                .collect(Collectors.groupingBy(
                        entry -> entry.getEntryDate().getMonth().toString(),
                        LinkedHashMap::new,
                        Collectors.counting()
                ));
        stats.put("entriesByMonth", entriesByMonth);
        
        // Total amount by status
        Map<JournalEntryStatus, BigDecimal> amountByStatus = entries.stream()
                .collect(Collectors.groupingBy(
                        JournalEntry::getStatus,
                        () -> new EnumMap<>(JournalEntryStatus.class),
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                JournalEntry::getTotalDebit,
                                BigDecimal::add
                        )
                ));
        stats.put("amountByStatus", amountByStatus);
        
        return stats;
    }
    
    /**
     * Get recurring entries
     */
    public List<JournalEntry> getRecurringEntries() throws ComptaException {
        return getAllEntries().stream()
                .filter(JournalEntry::isRecurring)
                .collect(Collectors.toList());
    }
    
    /**
     * Create automated entry for recurring transactions
     */
    public void processRecurringEntries() throws ComptaException {
        List<JournalEntry> recurringEntries = getRecurringEntries();
        
        // Using Stream with forEach to process recurring entries
        recurringEntries.stream()
                .filter(entry -> shouldCreateRecurring(entry))
                .forEach(entry -> {
                    try {
                        JournalEntry newEntry = cloneEntry(entry);
                        newEntry.setEntryDate(LocalDate.now());
                        newEntry.setEntryNumber(null); // Will be auto-generated
                        createEntry(newEntry);
                    } catch (ComptaException e) {
                        System.err.println("Error processing recurring entry: " + e.getMessage());
                    }
                });
    }
    
    private boolean shouldCreateRecurring(JournalEntry entry) {
        // Logic to determine if recurring entry should be created
        // Based on frequency and last creation date
        return true; // Simplified
    }
    
    private JournalEntry cloneEntry(JournalEntry original) {
        JournalEntry clone = new JournalEntry();
        clone.setDescription(original.getDescription());
        clone.setReference(original.getReference());
        clone.setRecurring(false); // Don't make the clone recurring
        
        // Clone lines using Stream
        original.getLines().stream()
                .map(line -> {
                    JournalEntryLine newLine = new JournalEntryLine();
                    newLine.setAccountCode(line.getAccountCode());
                    newLine.setAccountName(line.getAccountName());
                    newLine.setDebit(line.getDebit());
                    newLine.setCredit(line.getCredit());
                    newLine.setDescription(line.getDescription());
                    return newLine;
                })
                .forEach(clone::addLine);
        
        return clone;
    }
}

