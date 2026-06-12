<div align="center">

<img src="https://img.shields.io/badge/ComptaFX-v1.1-1e3a5f?style=for-the-badge&logo=data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCAyNCAyNCI+PHBhdGggZmlsbD0id2hpdGUiIGQ9Ik0xMiAyTDIgN2wxMCA1IDEwLTV6TTIgMTdsOSA1IDktNXYtN2wtOSA1LTktNXoiLz48L3N2Zz4=" alt="ComptaFX"/>

# ComptaFX — Accounting Management System

**A modern, full-featured desktop accounting application built with JavaFX**  
*Designed for Tunisian standards · Layered architecture · AI integration*

---

[![Java](https://img.shields.io/badge/Java-17-ED8B00?style=flat-square&logo=openjdk&logoColor=white)](https://openjdk.org/projects/jdk/17/)
[![JavaFX](https://img.shields.io/badge/JavaFX-21-4a90d9?style=flat-square&logo=java&logoColor=white)](https://openjfx.io/)
[![MySQL](https://img.shields.io/badge/MySQL-8.2-4479A1?style=flat-square&logo=mysql&logoColor=white)](https://www.mysql.com/)
[![Maven](https://img.shields.io/badge/Maven-3.11-C71A36?style=flat-square&logo=apachemaven&logoColor=white)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-MIT-2e7d32?style=flat-square)](LICENSE)
[![Platform](https://img.shields.io/badge/Platform-Windows%20%7C%20Linux%20%7C%20macOS-1e3a5f?style=flat-square&logo=windows&logoColor=white)](https://github.com)
[![Build](https://img.shields.io/badge/Build-Passing-4caf50?style=flat-square&logo=apache-maven)](https://github.com)
[![Currency](https://img.shields.io/badge/Currency-TND%20(Tunisian%20Dinar)-f57c00?style=flat-square)](https://github.com)

</div>

---

## 📽️ Demo

Watch the Demo Video:

https://github.com/user-attachments/assets/46a789dd-de0b-48d3-a921-f1f28c5cdd9f



---

## 📋 Table of Contents

- [🎯 About](#-about)
- [✨ Features](#-features)
- [🏗️ Architecture](#️-architecture)
- [🗃️ Data Model](#️-data-model)
- [🚀 Quick Start](#-quick-start)
- [⚙️ Configuration](#️-configuration)
- [📦 Tech Stack](#-tech-stack)
- [📁 Project Structure](#-project-structure)
- [🤖 AI Assistant](#-ai-assistant)
- [⌨️ Keyboard Shortcuts](#️-keyboard-shortcuts)
- [🗺️ Roadmap](#️-roadmap)
- [🤝 Contributing](#-contributing)

---

## 🎯 About

**ComptaFX** is a complete desktop accounting management application built with **JavaFX 21** and **Java 17**, designed for the needs of Tunisian SMEs. It covers the full accounting cycle — from journal entries to financial report generation, including third-party management (clients, suppliers) and treasury.

The project also demonstrates modern Java development best practices:

| Concept | Implementation |
|---------|----------------|
| **Layered Architecture** | DAO → Business Logic (Services) → Presentation (JavaFX) |
| **Design Patterns** | DAO, Service Layer, MVC, Singleton |
| **Java Streams & Lambdas** | Functional filtering, aggregation and sorting |
| **Java Collections** | `ObservableList`, `Map`, `TreeMap`, `LinkedHashMap` |
| **Exception Handling** | Custom `ComptaException` hierarchy |
| **JDBC with Transactions** | Rollback management, generated keys, batch inserts |
| **JPMS (Module System)** | Structured `module-info.java` |

---

## ✨ Features

### 📊 Dashboard
- Real-time overview of receivables and payables
- Recent invoices and journal entries tables
- Dynamic KPI cards

### 📝 Journal Entries
- Create entries with debit / credit lines
- Automatic balance validation (Σ debit = Σ credit)
- Status lifecycle: Draft → Pending → Posted → Reversed
- Recurring entries support
- Advanced search and filters

### 📄 Invoices
- Manage **customer invoices** (receivables) and **supplier invoices** (payables)
- Invoice lines with automatic VAT calculation
- Full lifecycle: Draft → Sent → Partially Paid → Paid → Overdue
- Aging report
- Automatic row coloring (overdue = red, paid = green)

### 👤 Clients
- Full client profile (Individual / Company)
- Tax ID, bank account (RIB), credit limit, balance
- Multi-criteria search (name, code, email, city)
- KPIs: total clients, active, by type
- Auto-generated client code (`CLI-00001`)

### 🏢 Suppliers
- Supplier profile with category and payment terms
- Outstanding supplier balance tracking
- Average payment delay calculated automatically
- Filter by category (Services, Raw materials, Equipment, etc.)

### 🏦 Treasury
- Multi-bank account management (TND, EUR, USD, GBP)
- Transactions: wire transfer, cheque, direct debit, cash, card
- Running balance recalculated automatically on each transaction
- Split view: accounts on the left, transactions on the right
- Cash flow coloring: inflows (green), outflows (red)
- Monthly statistics: inflows / outflows for the current month

### 📈 Financial Reports
- **Trial Balance** — aggregated by account with debit/credit/balance totals
- **Invoice Summary** — by status, amounts invoiced / paid / outstanding
- **Client Situation** — number of invoices, total billed, amount due
- **Supplier Situation** — symmetric view on the purchasing side
- Period filters: month, quarter, year, custom range

### 📚 Chart of Accounts
- Tunisian standard chart of accounts reference (4 classes)
- Quick account lookup

### ⚙️ Settings
- Company information (name, tax ID, address, email)
- Accounting defaults (currency, VAT rate, invoice prefix, payment delay)
- Live database connection tester
- Interface preferences (language, AI panel, notifications)
- Persisted via `java.util.prefs.Preferences` — no external config file needed

### 🤖 AI Assistant
- Built-in chat panel (collapsible right panel)
- Connects to **Ollama** locally (configurable model)
- Automatic fallback to cloud if Ollama is unavailable
- Accounting context pre-loaded in the system prompt

---

## 🏗️ Architecture

ComptaFX follows a strict **3-tier architecture** with clear separation of concerns:

```
┌─────────────────────────────────────────────────────────────────┐
│                     PRESENTATION LAYER                          │
│                       (JavaFX / FXML)                           │
│                                                                  │
│  MainController  ControleurTableauBord  ControleurFactures      │
│  ControleurClients  ControleurFournisseurs  ControleurTresorerie │
│  ControleurRapports  ControleurParametres  ChatController        │
└───────────────────────────┬─────────────────────────────────────┘
                            │  calls
┌───────────────────────────▼─────────────────────────────────────┐
│                     BUSINESS LAYER                               │
│                       (Services)                                 │
│                                                                  │
│  ServiceFacturesImpl   ServiceEcrituresImpl                     │
│  ServiceClientsImpl    ServiceFournisseursImpl                   │
│  ServiceTresorerieImpl                                           │
│                                                                  │
│  ► Business validation   ► Exception handling                   │
│  ► Application logic     ► Streams & Lambdas                    │
└───────────────────────────┬─────────────────────────────────────┘
                            │  delegates to
┌───────────────────────────▼─────────────────────────────────────┐
│                     DATA ACCESS LAYER                            │
│                         (DAOs)                                   │
│                                                                  │
│  InvoiceDAO      JournalEntryDAO   ClientDAO                    │
│  FournisseurDAO  CompteBancaireDAO                               │
│                                                                  │
│  ► JDBC / PreparedStatement   ► SQL Transactions                │
│  ► ResultSet → Object mapping ► Batch inserts                   │
└───────────────────────────┬─────────────────────────────────────┘
                            │
┌───────────────────────────▼─────────────────────────────────────┐
│                      DATABASE                                    │
│                   MySQL 8.x — comptafx                           │
└─────────────────────────────────────────────────────────────────┘
```

### FXML Navigation Flow

```
VuePrincipale.fxml (root BorderPane)
├── Top    → MenuBar + ToolBar (quick navigation)
├── Left   → Sidebar with sections: NAVIGATION / ACCOUNTING /
│            TRANSACTIONS / THIRD PARTIES / TREASURY /
│            REPORTS / REFERENCE / SETTINGS
├── Center → SplitPane
│   ├── [75%] StackPane contentArea — active view loaded dynamically
│   │         Dashboard | JournalEntries | Invoices |
│   │         Clients | Suppliers | Treasury |
│   │         Reports | ChartOfAccounts | Settings
│   └── [25%] ChatPanel.fxml (collapsible AI assistant)
└── Bottom → StatusBar (status, date, version)
```

---

## 🗃️ Data Model

### Database Schema

```
┌─────────────────────┐      ┌──────────────────────────┐
│   journal_entries   │      │   journal_entry_lines     │
├─────────────────────┤      ├──────────────────────────┤
│ id (PK)             │──┐   │ id (PK)                  │
│ entry_number        │  └──►│ journal_entry_id (FK)    │
│ entry_date          │      │ account_code             │
│ description         │      │ account_name             │
│ reference           │      │ debit                    │
│ status              │      │ credit                   │
│ is_recurring        │      │ line_number              │
│ created_at / by     │      └──────────────────────────┘
└─────────────────────┘

┌─────────────────────┐      ┌──────────────────────────┐
│      invoices       │      │      invoice_lines        │
├─────────────────────┤      ├──────────────────────────┤
│ id (PK)             │──┐   │ id (PK)                  │
│ invoice_number      │  └──►│ invoice_id (FK)          │
│ type (REC/PAY)      │      │ description              │
│ vendor_name         │      │ quantity                 │
│ customer_name       │      │ unit_price               │
│ invoice_date        │      │ tax_rate                 │
│ due_date / status   │      │ account_code             │
│ total / paid_amount │      └──────────────────────────┘
└─────────────────────┘

┌─────────────────────┐      ┌──────────────────────────┐
│   comptes_bancaires │      │  transactions_bancaires   │
├─────────────────────┤      ├──────────────────────────┤
│ id (PK)             │──┐   │ id (PK)                  │
│ banque, intitule    │  └──►│ compte_bancaire_id (FK)  │
│ solde_initial       │      │ date_transaction         │
│ solde_actuel        │      │ libelle, type            │
│ devise, actif       │      │ debit, credit            │
└─────────────────────┘      │ reference                │
                             └──────────────────────────┘

┌──────────────────────────┐   ┌──────────────────────────┐
│         clients          │   │      fournisseurs         │
├──────────────────────────┤   ├──────────────────────────┤
│ id, code, nom, type      │   │ id, code, nom, type      │
│ email, telephone, ville  │   │ email, telephone, ville  │
│ matricule_fiscal, rib    │   │ delai_paiement, solde    │
│ limite_credit, solde     │   │ categorie, actif         │
│ actif, created_at        │   └──────────────────────────┘
└──────────────────────────┘
```

### Java Entities

| Entity | Package | Description |
|--------|---------|-------------|
| `Invoice` | `entities` | Invoice (receivable or payable) with lines |
| `InvoiceLine` | `entities` | Invoice line item (qty, price, VAT) |
| `JournalEntry` | `entities` | Journal entry with debit/credit lines |
| `JournalEntryLine` | `entities` | Entry line (account, debit, credit) |
| `Client` | `entities` | Full client profile |
| `Fournisseur` | `entities` | Full supplier profile |
| `CompteBancaire` | `entities` | Bank account with computed balance |
| `TransactionBancaire` | `entities` | Bank transaction (debit or credit) |

---

## 🚀 Quick Start

### Prerequisites

| Tool | Min. Version | Download |
|------|-------------|----------|
| JDK | 17+ | [OpenJDK](https://adoptium.net/) |
| Maven | 3.8+ | [Apache Maven](https://maven.apache.org/download.cgi) |
| MySQL | 8.0+ | [MySQL Community](https://dev.mysql.com/downloads/) |
| Ollama *(optional)* | Latest | [ollama.com](https://ollama.com) |

### 1. Clone the repository

```bash
git clone https://github.com/MohamedHouij03/ComptaFX.git
cd ComptaFX
```

### 2. Set up MySQL

```sql
CREATE DATABASE IF NOT EXISTS comptafx
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

-- Optional: create a dedicated user
CREATE USER 'comptafx_user'@'localhost' IDENTIFIED BY 'comptafx_password';
GRANT ALL PRIVILEGES ON comptafx.* TO 'comptafx_user'@'localhost';
FLUSH PRIVILEGES;
```

> All tables are created **automatically** on first launch.

### 3. Configure the database connection

Edit [DatabaseConfig.java](src/main/java/com/comptafx/dao/DatabaseConfig.java) if your MySQL credentials differ from the defaults:

```java
connection = DriverManager.getConnection(
    "jdbc:mysql://localhost:3306/comptafx?useSSL=false&serverTimezone=UTC",
    "root",   // ← your MySQL user
    "root"    // ← your MySQL password
);
```

### 4. Build & Run

```bash
# Compile
mvn compile

# Launch
mvn javafx:run
```

Or build a runnable JAR:

```bash
mvn package
java --module-path /path/to/javafx-sdk/lib \
     --add-modules javafx.controls,javafx.fxml \
     -jar target/ComptaFX-1.0-SNAPSHOT.jar
```

### 5. Set up the AI assistant (optional)

```bash
# Install Ollama — Windows: download installer from https://ollama.com
# Linux / macOS:
curl -fsSL https://ollama.com/install.sh | sh

# Pull a lightweight model
ollama pull llama3.2

# Start Ollama
ollama serve
```

The app auto-detects Ollama on `http://localhost:11434` and falls back gracefully if unavailable.

---

## ⚙️ Configuration

Access **⚙ Settings** from the sidebar or toolbar:

| Section | Setting | Default |
|---------|---------|---------|
| Company | Company name | `Default Company` |
| Company | Tax ID | *(empty)* |
| Accounting | Currency | `TND` |
| Accounting | VAT rate | `19%` |
| Accounting | Invoice prefix | `FAC` |
| Accounting | Payment delay | `30 days` |
| Database | Host | `localhost` |
| Database | Port | `3306` |
| Database | Database name | `comptafx` |

Settings are persisted via `java.util.prefs.Preferences` — no external config file required.

---

## 📦 Tech Stack

<div align="center">

| Layer | Technology | Version | Role |
|-------|-----------|---------|------|
| **UI** | JavaFX | 21 | FXML + CSS graphical interface |
| **Language** | Java | 17 LTS | Business logic, Streams, Lambdas |
| **Build** | Apache Maven | 3.11 | Dependency management & build lifecycle |
| **Database** | MySQL | 8.2 | Relational persistence |
| **JDBC** | MySQL Connector/J | 8.2.0 | MySQL JDBC driver |
| **Export** | Apache POI | 5.2.5 | Excel export (`.xlsx`) |
| **Export** | iText 7 | 7.2.5 | PDF generation |
| **Import/Export** | OpenCSV | 5.9 | CSV read/write |
| **AI** | Ollama API | Latest | Local AI assistant (HTTP/JSON) |
| **Serialization** | Gson | 2.10.1 | JSON handling (Ollama responses) |
| **HTTP** | java.net.http | JDK 17 | Native HTTP client (Ollama calls) |
| **Preferences** | java.util.prefs | JDK 17 | User settings persistence |
| **Module System** | JPMS | JDK 17 | `module-info.java` |

</div>

---

## 📁 Project Structure

```
ComptaFX/
│
├── pom.xml                                 ← Maven configuration
├── README.md
│
└── src/main/
    ├── java/
    │   ├── module-info.java                ← JPMS module declaration
    │   └── com/comptafx/
    │       │
    │       ├── ApplicationPrincipale.java  ← Entry point (main)
    │       │
    │       ├── entities/                   ← Data models (POJOs)
    │       │   ├── Invoice.java
    │       │   ├── InvoiceLine.java
    │       │   ├── InvoiceStatus.java
    │       │   ├── InvoiceType.java
    │       │   ├── JournalEntry.java
    │       │   ├── JournalEntryLine.java
    │       │   ├── JournalEntryStatus.java
    │       │   ├── Client.java             ✨
    │       │   ├── TypeClient.java         ✨
    │       │   ├── Fournisseur.java        ✨
    │       │   ├── CompteBancaire.java     ✨
    │       │   ├── TransactionBancaire.java ✨
    │       │   └── TypeTransaction.java    ✨
    │       │
    │       ├── dao/                        ← Data access (JDBC)
    │       │   ├── DatabaseConfig.java
    │       │   ├── DatabaseException.java
    │       │   ├── IInvoiceDAO.java / InvoiceDAO.java
    │       │   ├── IJournalEntryDAO.java / JournalEntryDAO.java
    │       │   ├── IClientDAO.java / ClientDAO.java         ✨
    │       │   ├── IFournisseurDAO.java / FournisseurDAO.java ✨
    │       │   └── ICompteBancaireDAO.java / CompteBancaireDAO.java ✨
    │       │
    │       ├── metier/                     ← Business logic (Services)
    │       │   ├── Auditable.java
    │       │   ├── ComptaException.java
    │       │   ├── ServiceFactures.java / ServiceFacturesImpl.java
    │       │   ├── ServiceEcritures.java / ServiceEcrituresImpl.java
    │       │   ├── ServiceClients.java / ServiceClientsImpl.java     ✨
    │       │   ├── ServiceFournisseurs.java / ServiceFournisseursImpl.java ✨
    │       │   └── ServiceTresorerie.java / ServiceTresorerieImpl.java ✨
    │       │
    │       ├── presentation/               ← JavaFX controllers
    │       │   ├── MainController.java
    │       │   ├── ChatController.java
    │       │   ├── ControleurTableauBord.java
    │       │   ├── ControleurEcritures.java
    │       │   ├── ControleurFactures.java
    │       │   ├── ControleurPlanComptable.java
    │       │   ├── ControleurClients.java      ✨
    │       │   ├── ControleurFournisseurs.java ✨
    │       │   ├── ControleurTresorerie.java   ✨
    │       │   ├── ControleurRapports.java     ✨
    │       │   └── ControleurParametres.java   ✨
    │       │
    │       └── ai/
    │           ├── OllamaService.java
    │           └── CodeAccessService.java
    │
    └── resources/
        ├── fxml/
        │   ├── VuePrincipale.fxml          ← Root layout (BorderPane)
        │   ├── VueTableauBord.fxml
        │   ├── VueEcritures.fxml
        │   ├── VueFactures.fxml
        │   ├── VuePlanComptable.fxml
        │   ├── ChatPanel.fxml
        │   ├── VueClients.fxml             ✨
        │   ├── VueFournisseurs.fxml        ✨
        │   ├── VueTresorerie.fxml          ✨
        │   ├── VueRapports.fxml            ✨
        │   └── VueParametres.fxml          ✨
        │
        └── css/
            └── styles.css                  ← Full design system (749 lines)
```

> ✨ = Added in v1.1

---

## 🤖 AI Assistant

ComptaFX includes an intelligent accounting assistant accessible from the collapsible right panel.

```
User types a question
        │
        ▼
  OllamaService.java
  POST /api/generate
  model: llama3.2
        │
   ┌────┴────┐
   │         │
Ollama    Ollama
available unavailable
   │         │
Local     Generic
response  fallback
```

**Example questions:**

```
"How do I record a VAT journal entry?"
"What is the difference between a receivable and a payable?"
"How do I mark an invoice as paid?"
"Explain the Tunisian chart of accounts class 1"
```

---

## ⌨️ Keyboard Shortcuts

| Shortcut | Action |
|----------|--------|
| `F5` | Refresh current view |
| `F1` | Open help / documentation |
| `F11` | Toggle fullscreen |
| `Ctrl + +` | Zoom in |
| `Ctrl + -` | Zoom out |
| `Ctrl + 0` | Reset zoom (100%) |

---

## 🎨 Design System

The app uses a consistent **professional blue theme** defined through JavaFX CSS variables:

```css
-primary-color:  #1e3a5f   /* Navy blue — sidebar, menu bar */
-accent-color:   #4a90d9   /* Light blue — buttons, focus ring */
-success-color:  #2e7d32   /* Green — paid status, positive balance */
-warning-color:  #f57c00   /* Orange — pending, warning */
-danger-color:   #c62828   /* Red — overdue, delete actions */
-bg-primary:     #f5f7fa   /* Main background */
-bg-secondary:   #ffffff   /* Cards, tables */
-text-primary:   #2c3e50   /* Main text */
```

Available component classes: `primary-btn`, `secondary-btn`, `danger-btn`, `kpi-card`, `data-table`, `page-title`, `table-card`, `menu-btn`, `toolbar-btn`, `status-bar`, `page-status-bar`.

---

## 🗺️ Roadmap

### v1.1 *(current)*
- [x] Client management
- [x] Supplier management
- [x] Treasury & bank accounts module
- [x] Financial reports (trial balance, invoices, third parties)
- [x] Settings page with persistence

### v1.2 *(planned)*
- [ ] PDF export for financial reports
- [ ] Excel export for tables (clients, invoices, balance)
- [ ] Invoice printing (iText 7)
- [ ] Multi-fiscal-year management
- [ ] Enhanced dashboard with JavaFX Charts

### v2.0 *(future)*
- [ ] Multi-user mode with authentication
- [ ] Role and permissions management
- [ ] Database backup / restore
- [ ] Optional cloud sync
- [ ] Dark theme

---

## 🤝 Contributing

Contributions are welcome!

```bash
# 1. Fork the repository
# 2. Create a feature branch
git checkout -b feature/my-feature

# 3. Commit your changes
git commit -m "feat: add my feature"

# 4. Push the branch
git push origin feature/my-feature

# 5. Open a Pull Request
```

**Code standards:**
- Class names in PascalCase, methods and variables in camelCase
- Respect the DAO / Business / Presentation separation
- Always propagate exceptions via `ComptaException` in the business layer
- Any UI update from a non-JavaFX thread must go through `Platform.runLater()`

---

## 📝 License

```
MIT License — Copyright (c) 2026 Mohamed Houij

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND.
```

---

## 👤 Author

<div align="center">

**Mohamed Houij**

[![GitHub](https://img.shields.io/badge/GitHub-MohamedHouij03-181717?style=flat-square&logo=github)](https://github.com/MohamedHouij03)
[![Email](https://img.shields.io/badge/Email-mohamed.houij700%40gmail.com-EA4335?style=flat-square&logo=gmail&logoColor=white)](mailto:mohamed.houij700@gmail.com)

*Built with ❤️ in Java · Tunis, Tunisia*

</div>

---

<div align="center">

[![Java](https://img.shields.io/badge/Java-17-ED8B00?style=flat-square&logo=openjdk&logoColor=white)](https://openjdk.org/)
[![JavaFX](https://img.shields.io/badge/JavaFX-21-4a90d9?style=flat-square&logo=java&logoColor=white)](https://openjfx.io/)
[![MySQL](https://img.shields.io/badge/MySQL-8.2-4479A1?style=flat-square&logo=mysql&logoColor=white)](https://www.mysql.com/)
[![Maven](https://img.shields.io/badge/Maven-3.11-C71A36?style=flat-square&logo=apachemaven&logoColor=white)](https://maven.apache.org/)

*⭐ If this project was helpful, consider leaving a star!*

</div>
