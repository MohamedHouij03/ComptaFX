package com.comptafx.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Database configuration and connection management
 * Uses MySQL for production deployment
 * 
 * SETUP INSTRUCTIONS:
 * 1. Install MySQL Server
 * 2. Create database: CREATE DATABASE comptafx CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
 * 3. Create user: CREATE USER 'comptafx_user'@'localhost' IDENTIFIED BY 'comptafx_password';
 * 4. Grant privileges: GRANT ALL PRIVILEGES ON comptafx.* TO 'comptafx_user'@'localhost';
 * 5. Flush: FLUSH PRIVILEGES;
 */
public class DatabaseConfig {
    
    // MySQL Connection Settings
    private static volatile Connection connection = null;
    
    private DatabaseConfig() {}
    
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                synchronized (DatabaseConfig.class) {
                    if (connection == null || connection.isClosed()) {
                        // Load MySQL JDBC driver
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        connection = DriverManager.getConnection(
                                "jdbc:mysql://localhost:3306/comptafx?useSSL=false&serverTimezone=UTC&createDatabaseIfNotExist=true",
                                "root",
                                "root"
                        );
                    }
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Database connection error: " + e.getMessage());
            e.printStackTrace();
        }
        return connection;
    }
    
    public static DatabaseConfig getInstance() {
        return new DatabaseConfig();
    }
    
    public static void initializeDatabase() throws DatabaseException {
        try (Statement stmt = getConnection().createStatement()) {
            
            // Create Journal Entries table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS journal_entries (
                    id BIGINT PRIMARY KEY AUTO_INCREMENT,
                    entry_number VARCHAR(50) NOT NULL,
                    entry_date DATE NOT NULL,
                    description TEXT,
                    reference VARCHAR(100),
                    status VARCHAR(20) DEFAULT 'DRAFT',
                    is_recurring TINYINT(1) DEFAULT 0,
                    recurring_frequency VARCHAR(50),
                    client_id BIGINT,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                    created_by VARCHAR(100),
                    updated_by VARCHAR(100),
                    UNIQUE KEY uk_entry_number_client (entry_number, client_id)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
            """);
            
            // Create Journal Entry Lines table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS journal_entry_lines (
                    id BIGINT PRIMARY KEY AUTO_INCREMENT,
                    journal_entry_id BIGINT NOT NULL,
                    account_code VARCHAR(50) NOT NULL,
                    account_name VARCHAR(255),
                    description TEXT,
                    debit DECIMAL(15,2) DEFAULT 0.00,
                    credit DECIMAL(15,2) DEFAULT 0.00,
                    line_number INT,
                    FOREIGN KEY (journal_entry_id) REFERENCES journal_entries(id) ON DELETE CASCADE
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
            """);
            
            // Create Invoices table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS invoices (
                    id BIGINT PRIMARY KEY AUTO_INCREMENT,
                    invoice_number VARCHAR(50) NOT NULL,
                    type VARCHAR(30) NOT NULL,
                    vendor_id BIGINT,
                    customer_id BIGINT,
                    vendor_name VARCHAR(255),
                    customer_name VARCHAR(255),
                    invoice_date DATE NOT NULL,
                    due_date DATE,
                    payment_date DATE,
                    status VARCHAR(30) DEFAULT 'DRAFT',
                    subtotal DECIMAL(15,2) DEFAULT 0.00,
                    tax_amount DECIMAL(15,2) DEFAULT 0.00,
                    total_amount DECIMAL(15,2) DEFAULT 0.00,
                    paid_amount DECIMAL(15,2) DEFAULT 0.00,
                    currency VARCHAR(10) DEFAULT 'EUR',
                    notes TEXT,
                    client_id BIGINT,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                    created_by VARCHAR(100),
                    updated_by VARCHAR(100),
                    UNIQUE KEY uk_invoice_number_client (invoice_number, client_id)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
            """);
            
            // Create Invoice Lines table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS invoice_lines (
                    id BIGINT PRIMARY KEY AUTO_INCREMENT,
                    invoice_id BIGINT NOT NULL,
                    description TEXT,
                    quantity DECIMAL(15,4) DEFAULT 1.0000,
                    unit_price DECIMAL(15,2) DEFAULT 0.00,
                    tax_rate DECIMAL(5,2) DEFAULT 0.00,
                    account_code VARCHAR(50),
                    line_number INT,
                    FOREIGN KEY (invoice_id) REFERENCES invoices(id) ON DELETE CASCADE
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
            """);
            
            // Create Clients table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS clients (
                    id BIGINT PRIMARY KEY AUTO_INCREMENT,
                    code VARCHAR(20),
                    nom VARCHAR(255) NOT NULL,
                    type VARCHAR(20) DEFAULT 'ENTREPRISE',
                    email VARCHAR(255),
                    telephone VARCHAR(50),
                    adresse TEXT,
                    ville VARCHAR(100),
                    code_postal VARCHAR(20),
                    pays VARCHAR(100) DEFAULT 'Tunisie',
                    matricule_fiscal VARCHAR(100),
                    rib VARCHAR(100),
                    limite_credit DECIMAL(15,2) DEFAULT 0.00,
                    solde DECIMAL(15,2) DEFAULT 0.00,
                    notes TEXT,
                    actif TINYINT(1) DEFAULT 1,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                    created_by VARCHAR(100),
                    updated_by VARCHAR(100)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
            """);

            // Create Fournisseurs table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS fournisseurs (
                    id BIGINT PRIMARY KEY AUTO_INCREMENT,
                    code VARCHAR(20),
                    nom VARCHAR(255) NOT NULL,
                    type VARCHAR(20) DEFAULT 'ENTREPRISE',
                    email VARCHAR(255),
                    telephone VARCHAR(50),
                    adresse TEXT,
                    ville VARCHAR(100),
                    code_postal VARCHAR(20),
                    pays VARCHAR(100) DEFAULT 'Tunisie',
                    matricule_fiscal VARCHAR(100),
                    rib VARCHAR(100),
                    delai_paiement INT DEFAULT 30,
                    solde DECIMAL(15,2) DEFAULT 0.00,
                    categorie VARCHAR(100),
                    notes TEXT,
                    actif TINYINT(1) DEFAULT 1,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                    created_by VARCHAR(100),
                    updated_by VARCHAR(100)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
            """);

            // Create Comptes Bancaires table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS comptes_bancaires (
                    id BIGINT PRIMARY KEY AUTO_INCREMENT,
                    banque VARCHAR(255) NOT NULL,
                    intitule VARCHAR(255) NOT NULL,
                    numero_compte VARCHAR(100),
                    rib VARCHAR(100),
                    solde_initial DECIMAL(15,2) DEFAULT 0.00,
                    solde_actuel DECIMAL(15,2) DEFAULT 0.00,
                    devise VARCHAR(10) DEFAULT 'TND',
                    actif TINYINT(1) DEFAULT 1,
                    notes TEXT,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
            """);

            // Create Transactions Bancaires table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS transactions_bancaires (
                    id BIGINT PRIMARY KEY AUTO_INCREMENT,
                    compte_bancaire_id BIGINT NOT NULL,
                    date_transaction DATE NOT NULL,
                    libelle VARCHAR(500),
                    debit DECIMAL(15,2) DEFAULT 0.00,
                    credit DECIMAL(15,2) DEFAULT 0.00,
                    reference VARCHAR(100),
                    type VARCHAR(30) DEFAULT 'VIREMENT',
                    notes TEXT,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (compte_bancaire_id) REFERENCES comptes_bancaires(id) ON DELETE CASCADE
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
            """);

            // Create indexes for better performance (ignore if already exists)
            createIndexIfNotExists(stmt, "idx_journal_entries_client", "journal_entries", "client_id");
            createIndexIfNotExists(stmt, "idx_journal_entries_date", "journal_entries", "entry_date");
            createIndexIfNotExists(stmt, "idx_journal_entries_status", "journal_entries", "status");
            createIndexIfNotExists(stmt, "idx_invoices_client", "invoices", "client_id");
            createIndexIfNotExists(stmt, "idx_invoices_status", "invoices", "status");
            createIndexIfNotExists(stmt, "idx_invoices_date", "invoices", "invoice_date");
            createIndexIfNotExists(stmt, "idx_invoices_due_date", "invoices", "due_date");
            createIndexIfNotExists(stmt, "idx_clients_nom", "clients", "nom");
            createIndexIfNotExists(stmt, "idx_clients_actif", "clients", "actif");
            createIndexIfNotExists(stmt, "idx_fournisseurs_nom", "fournisseurs", "nom");
            createIndexIfNotExists(stmt, "idx_transactions_compte", "transactions_bancaires", "compte_bancaire_id");
            createIndexIfNotExists(stmt, "idx_transactions_date", "transactions_bancaires", "date_transaction");

            System.out.println("MySQL Database initialized successfully!");
            
        } catch (SQLException e) {
            throw new DatabaseException("Failed to initialize MySQL database: " + e.getMessage(), e);
        }
    }
    
    /**
     * Helper method to create index if it doesn't exist (MySQL compatible)
     */
    private static void createIndexIfNotExists(Statement stmt, String indexName, String tableName, String columnName) {
        try {
            stmt.execute("CREATE INDEX " + indexName + " ON " + tableName + "(" + columnName + ")");
        } catch (SQLException e) {
            // Index already exists - ignore error (MySQL error code 1061)
            if (!e.getMessage().contains("Duplicate key name")) {
                System.err.println("Warning creating index " + indexName + ": " + e.getMessage());
            }
        }
    }
    
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                connection = null;
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
    
    /**
     * Test the database connection
     */
    public static boolean testConnection() {
        try {
            Connection conn = getConnection();
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Connection test failed: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Drop unnecessary tables from the database
     * Removes bank_accounts and bank_transactions tables that are not used in the application
     */
    public static void dropUnnecessaryTables() throws DatabaseException {
        try (Statement stmt = getConnection().createStatement()) {
            // Drop bank_transactions first (has foreign key to bank_accounts)
            try {
                stmt.execute("DROP TABLE IF EXISTS bank_transactions");
                System.out.println("Dropped table: bank_transactions");
            } catch (SQLException e) {
                System.err.println("Warning: Could not drop bank_transactions: " + e.getMessage());
            }
            
            // Drop bank_accounts
            try {
                stmt.execute("DROP TABLE IF EXISTS bank_accounts");
                System.out.println("Dropped table: bank_accounts");
            } catch (SQLException e) {
                System.err.println("Warning: Could not drop bank_accounts: " + e.getMessage());
            }
            
            System.out.println("Unnecessary tables dropped successfully!");
        } catch (SQLException e) {
            throw new DatabaseException("Failed to drop unnecessary tables: " + e.getMessage(), e);
        }
    }
}
