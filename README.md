<div align="center">

<img src="https://img.shields.io/badge/ComptaFX-v1.0-1e3a5f?style=for-the-badge&logo=data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCAyNCAyNCI+PHBhdGggZmlsbD0id2hpdGUiIGQ9Ik0xMiAyTDIgN2wxMCA1IDEwLTV6TTIgMTdsOSA1IDktNXYtN2wtOSA1LTktNXoiLz48L3N2Zz4=" alt="ComptaFX"/>

# ComptaFX — Système de Gestion Comptable

**Application comptable de bureau, moderne et complète, développée en JavaFX**  
*Conçue pour les standards tunisiens · Architecture en couches · Intégration IA*

---

[![Java](https://img.shields.io/badge/Java-17-ED8B00?style=flat-square&logo=openjdk&logoColor=white)](https://openjdk.org/projects/jdk/17/)
[![JavaFX](https://img.shields.io/badge/JavaFX-21-4a90d9?style=flat-square&logo=java&logoColor=white)](https://openjfx.io/)
[![MySQL](https://img.shields.io/badge/MySQL-8.2-4479A1?style=flat-square&logo=mysql&logoColor=white)](https://www.mysql.com/)
[![Maven](https://img.shields.io/badge/Maven-3.11-C71A36?style=flat-square&logo=apachemaven&logoColor=white)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-MIT-2e7d32?style=flat-square)](LICENSE)
[![Platform](https://img.shields.io/badge/Platform-Windows%20%7C%20Linux%20%7C%20macOS-1e3a5f?style=flat-square&logo=windows&logoColor=white)](https://github.com)
[![Build](https://img.shields.io/badge/Build-Passing-4caf50?style=flat-square&logo=apache-maven)](https://github.com)
[![Devise](https://img.shields.io/badge/Devise-TND%20(Dinar%20Tunisien)-f57c00?style=flat-square)](https://github.com)

</div>

---

## 📽️ Démonstration

> **🎬 Vidéo de démonstration**

<div align="center">

```
╔══════════════════════════════════════════════════════════════════╗
║                                                                  ║
║                     🎬  DEMO VIDEO                               ║
║                                                                  ║
║   Lien : [À ajouter — enregistrez une démo avec OBS Studio]     ║
║                                                                  ║
║   Contenu suggéré de la démo (3-5 minutes) :                    ║
║   ① Lancement de l'application & navigation                      ║
║   ② Création d'un client et d'un fournisseur                     ║
║   ③ Saisie d'une facture client                                  ║
║   ④ Écriture comptable associée                                  ║
║   ⑤ Vue trésorerie & transactions bancaires                      ║
║   ⑥ Génération des rapports financiers                           ║
║   ⑦ Panneau IA (assistant comptable)                             ║
║                                                                  ║
╚══════════════════════════════════════════════════════════════════╝
```

*Remplacez cette section par un GIF animé ou une miniature YouTube une fois la vidéo enregistrée.*  
*Outils recommandés : [OBS Studio](https://obsproject.com/), [LiceCap](https://www.cockos.com/licecap/) (GIF)*

</div>

---

## 📋 Table des Matières

- [🎯 À Propos](#-à-propos)
- [✨ Fonctionnalités](#-fonctionnalités)
- [🖥️ Captures d'Écran](#️-captures-décran)
- [🏗️ Architecture](#️-architecture)
- [🗃️ Modèle de Données](#️-modèle-de-données)
- [🚀 Démarrage Rapide](#-démarrage-rapide)
- [⚙️ Configuration](#️-configuration)
- [📦 Technologies](#-technologies)
- [📁 Structure du Projet](#-structure-du-projet)
- [🤖 Assistant IA](#-assistant-ia)
- [⌨️ Raccourcis Clavier](#️-raccourcis-clavier)
- [🗺️ Feuille de Route](#️-feuille-de-route)
- [🤝 Contribution](#-contribution)

---

## 🎯 À Propos

**ComptaFX** est une application de bureau complète de gestion comptable, développée en **JavaFX 21** et **Java 17**, pensée pour les besoins des PME tunisiennes. Elle couvre l'ensemble du cycle comptable : de la saisie des écritures à la génération de rapports financiers, en passant par la gestion des tiers (clients, fournisseurs) et la trésorerie.

Le projet illustre également des bonnes pratiques de développement Java moderne :

| Concept | Implémentation |
|---------|----------------|
| **Architecture en couches** | DAO → Métier (Services) → Présentation (JavaFX) |
| **Design Patterns** | DAO, Service Layer, MVC, Singleton |
| **Java Streams & Lambdas** | Filtrage, agrégation et tri fonctionnels |
| **Collections Java** | `ObservableList`, `Map`, `TreeMap`, `LinkedHashMap` |
| **Gestion d'exceptions** | Hiérarchie custom `ComptaException` |
| **JDBC avec transactions** | Gestion des rollbacks, clés générées, batch |
| **JPMS (Module System)** | `module-info.java` structuré |

---

## ✨ Fonctionnalités

### 📊 Tableau de Bord
- Vue synthétique des créances et dettes en temps réel
- Tableaux des factures et écritures récentes
- Indicateurs clés (KPI) avec mise à jour dynamique

### 📝 Écritures Comptables
- Création d'écritures avec lignes débit / crédit
- Validation automatique de l'équilibre (Σ débit = Σ crédit)
- Statuts : Brouillon → En attente → Validée → Contrepassée
- Écritures récurrentes
- Recherche et filtres avancés

### 📄 Factures
- Gestion des factures **clients** (créances) et **fournisseurs** (dettes)
- Lignes de facturation avec calcul automatique TVA
- Cycle de vie complet : Brouillon → Envoyée → Partiellement payée → Payée → En retard
- Rapport d'échéancier (aging report)
- Coloration automatique des lignes (en retard = rouge, payée = vert)

### 👤 Clients
- Fiche client complète (Particulier / Entreprise)
- Matricule fiscal, RIB, limite de crédit, solde
- Recherche multi-critères (nom, code, email, ville)
- Indicateurs : total clients, actifs, par type
- Génération automatique de code client (`CLI-00001`)

### 🏢 Fournisseurs
- Fiche fournisseur avec catégorie, délai de paiement
- Suivi des encours fournisseurs
- Délai de paiement moyen calculé automatiquement
- Filtrage par catégorie (Services, Matières premières, etc.)

### 🏦 Trésorerie
- Gestion multi-comptes bancaires (TND, EUR, USD, GBP)
- Transactions : virement, chèque, prélèvement, espèces, carte
- Solde calculé automatiquement à chaque transaction
- Vue split : comptes à gauche, transactions à droite
- Coloration des flux : entrées (vert), sorties (rouge)
- Statistiques mensuelles : entrées / sorties du mois

### 📈 Rapports Financiers
- **Balance de vérification** — agrégation par compte, totaux débit/crédit/solde
- **Synthèse factures** — par statut, montants facturés / payés / restants
- **Situation clients** — nombre de factures, total facturé, solde dû
- **Situation fournisseurs** — vue symétrique côté achats
- Filtres de période : mois, trimestre, année, période personnalisée

### 📚 Plan Comptable
- Plan comptable tunisien de référence (4 classes)
- Consultation rapide des comptes normalisés

### ⚙️ Paramètres
- Informations société (raison sociale, MF, adresse, email)
- Paramètres comptables (devise, taux TVA, préfixe factures, délai paiement)
- Connexion base de données avec test en direct
- Préférences interface (langue, panneau IA, notifications)
- Persistance via `java.util.prefs.Preferences` (sans fichier de config externe)

### 🤖 Assistant IA
- Panneau de chat intégré (panneau droit rétractable)
- Connexion à **Ollama** en local (modèle configurable)
- Repli automatique vers une réponse cloud si Ollama indisponible
- Contexte comptable pré-chargé dans le prompt système

---

## 🖥️ Captures d'Écran

> *Ajoutez vos captures dans le dossier `docs/screenshots/` et remplacez les chemins ci-dessous.*

<div align="center">

| Tableau de Bord | Gestion des Clients |
|:-:|:-:|
| ![Dashboard](docs/screenshots/dashboard.png) | ![Clients](docs/screenshots/clients.png) |

| Trésorerie | Rapports Financiers |
|:-:|:-:|
| ![Tresorerie](docs/screenshots/tresorerie.png) | ![Rapports](docs/screenshots/rapports.png) |

| Saisie de Facture | Paramètres |
|:-:|:-:|
| ![Factures](docs/screenshots/factures.png) | ![Parametres](docs/screenshots/parametres.png) |

</div>

---

## 🏗️ Architecture

ComptaFX applique une **architecture 3-tiers stricte** avec séparation nette des responsabilités :

```
┌─────────────────────────────────────────────────────────────────┐
│                      COUCHE PRÉSENTATION                        │
│                        (JavaFX / FXML)                          │
│                                                                  │
│  MainController  ControleurTableauBord  ControleurFactures      │
│  ControleurClients  ControleurFournisseurs  ControleurTresorerie │
│  ControleurRapports  ControleurParametres  ChatController        │
└───────────────────────────┬─────────────────────────────────────┘
                            │  appelle
┌───────────────────────────▼─────────────────────────────────────┐
│                      COUCHE MÉTIER                               │
│                        (Services)                                │
│                                                                  │
│  ServiceFacturesImpl   ServiceEcrituresImpl                     │
│  ServiceClientsImpl    ServiceFournisseursImpl                   │
│  ServiceTresorerieImpl                                           │
│                                                                  │
│  ► Validation métier    ► Gestion d'exceptions                  │
│  ► Logique applicative  ► Streams & Lambdas                     │
└───────────────────────────┬─────────────────────────────────────┘
                            │  délègue
┌───────────────────────────▼─────────────────────────────────────┐
│                      COUCHE DAO                                  │
│                     (Accès aux Données)                          │
│                                                                  │
│  InvoiceDAO      JournalEntryDAO   ClientDAO                    │
│  FournisseurDAO  CompteBancaireDAO                               │
│                                                                  │
│  ► JDBC / PreparedStatement   ► Transactions SQL                │
│  ► Mapping ResultSet → Objet  ► Batch inserts                   │
└───────────────────────────┬─────────────────────────────────────┘
                            │
┌───────────────────────────▼─────────────────────────────────────┐
│                      BASE DE DONNÉES                             │
│                      MySQL 8.x — comptafx                        │
└─────────────────────────────────────────────────────────────────┘
```

### Flux de navigation (FXML)

```
VuePrincipale.fxml (BorderPane racine)
├── Top    → MenuBar + ToolBar (navigation rapide)
├── Left   → Sidebar avec sections NAVIGATION / COMPTABILITÉ /
│            TRANSACTIONS / TIERS / TRÉSORERIE / RAPPORTS /
│            RÉFÉRENCE / PARAMÈTRES
├── Center → SplitPane
│   ├── [75%] StackPane contentArea — vue active chargée dynamiquement
│   │         VueTableauBord | VueEcritures | VueFactures |
│   │         VueClients | VueFournisseurs | VueTresorerie |
│   │         VueRapports | VuePlanComptable | VueParametres
│   └── [25%] ChatPanel.fxml (assistant IA rétractable)
└── Bottom → StatusBar (statut, date, version)
```

---

## 🗃️ Modèle de Données

### Schéma de la base de données

```sql
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
│ due_date            │      │ account_code             │
│ status              │      └──────────────────────────┘
│ total_amount        │
│ paid_amount         │      ┌──────────────────────────┐
└─────────────────────┘      │         clients          │
                             ├──────────────────────────┤
┌─────────────────────┐      │ id, code, nom, type      │
│   comptes_bancaires │      │ email, telephone, ville  │
├─────────────────────┤      │ matricule_fiscal, rib    │
│ id (PK)             │──┐   │ limite_credit, solde     │
│ banque              │  │   │ actif, created_at        │
│ intitule            │  │   └──────────────────────────┘
│ solde_initial       │  │
│ solde_actuel        │  │   ┌──────────────────────────┐
└─────────────────────┘  │   │      fournisseurs        │
                         │   ├──────────────────────────┤
┌─────────────────────┐  │   │ id, code, nom, type      │
│transactions_bancaire│  │   │ email, telephone, ville  │
├─────────────────────┤  │   │ delai_paiement, solde    │
│ id (PK)             │  │   │ categorie, actif         │
│ compte_id (FK)      │◄─┘   └──────────────────────────┘
│ date_transaction    │
│ libelle, type       │
│ debit, credit       │
│ reference           │
└─────────────────────┘
```

### Entités Java

| Entité | Package | Description |
|--------|---------|-------------|
| `Invoice` | `entities` | Facture (créance ou dette) avec lignes |
| `InvoiceLine` | `entities` | Ligne de facturation (qté, prix, TVA) |
| `JournalEntry` | `entities` | Écriture comptable avec lignes débit/crédit |
| `JournalEntryLine` | `entities` | Ligne d'écriture (compte, débit, crédit) |
| `Client` | `entities` | Fiche client complète |
| `Fournisseur` | `entities` | Fiche fournisseur |
| `CompteBancaire` | `entities` | Compte bancaire avec solde calculé |
| `TransactionBancaire` | `entities` | Mouvement bancaire (débit ou crédit) |

---

## 🚀 Démarrage Rapide

### Prérequis

| Outil | Version minimale | Téléchargement |
|-------|-----------------|----------------|
| JDK | 17+ | [OpenJDK](https://adoptium.net/) |
| Maven | 3.8+ | [Apache Maven](https://maven.apache.org/download.cgi) |
| MySQL | 8.0+ | [MySQL Community](https://dev.mysql.com/downloads/) |
| Ollama *(optionnel)* | Latest | [ollama.com](https://ollama.com) |

### 1. Cloner le dépôt

```bash
git clone https://github.com/MohamedHouij03/ComptaFX.git
cd ComptaFX
```

### 2. Configurer MySQL

```sql
-- Connectez-vous à MySQL en tant que root
CREATE DATABASE IF NOT EXISTS comptafx
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

-- (Optionnel) Créer un utilisateur dédié
CREATE USER 'comptafx_user'@'localhost' IDENTIFIED BY 'comptafx_password';
GRANT ALL PRIVILEGES ON comptafx.* TO 'comptafx_user'@'localhost';
FLUSH PRIVILEGES;
```

> Les tables sont créées **automatiquement** au premier démarrage de l'application.

### 3. Configurer la connexion (si nécessaire)

Modifiez [DatabaseConfig.java](src/main/java/com/comptafx/dao/DatabaseConfig.java) si vos identifiants MySQL diffèrent des valeurs par défaut :

```java
// Ligne ~34 dans DatabaseConfig.java
connection = DriverManager.getConnection(
    "jdbc:mysql://localhost:3306/comptafx?useSSL=false&serverTimezone=UTC&createDatabaseIfNotExist=true",
    "root",    // ← votre utilisateur MySQL
    "root"     // ← votre mot de passe MySQL
);
```

### 4. Compiler & Lancer

```bash
# Compiler le projet
mvn compile

# Lancer l'application
mvn javafx:run
```

Ou via un IDE (IntelliJ IDEA / Eclipse) :

```bash
# IntelliJ IDEA : Run > Edit Configurations > Maven Goal
mvn javafx:run

# Ou construire un JAR exécutable
mvn package
java --module-path /path/to/javafx-sdk/lib \
     --add-modules javafx.controls,javafx.fxml \
     -jar target/ComptaFX-1.0-SNAPSHOT.jar
```

### 5. Configurer l'assistant IA (optionnel)

```bash
# Installer Ollama
# Windows : télécharger l'installeur sur https://ollama.com
# Linux / macOS :
curl -fsSL https://ollama.com/install.sh | sh

# Télécharger un modèle comptable (léger)
ollama pull llama3.2

# Lancer Ollama (en arrière-plan)
ollama serve
```

L'application détecte automatiquement Ollama sur `http://localhost:11434` et bascule en mode cloud si indisponible.

---

## ⚙️ Configuration

### Paramètres via l'interface

Accédez à **⚙ Paramètres** depuis la barre latérale ou la toolbar :

| Section | Paramètre | Valeur par défaut |
|---------|-----------|-------------------|
| Société | Raison sociale | `Société par Défaut` |
| Société | Matricule fiscal | *(vide)* |
| Comptable | Devise principale | `TND` |
| Comptable | Taux TVA | `19 %` |
| Comptable | Préfixe factures | `FAC` |
| Comptable | Délai paiement | `30 jours` |
| Base de données | Hôte | `localhost` |
| Base de données | Port | `3306` |
| Base de données | Base | `comptafx` |

Les paramètres sont persistés dans le registre système via `java.util.prefs.Preferences` — aucun fichier de configuration externe n'est nécessaire.

### Variables d'environnement (avancé)

```bash
# Surcharger la connexion MySQL via la CLI Java
java -Dcomptafx.db.url="jdbc:mysql://monserveur:3306/comptafx" \
     -Dcomptafx.db.user="monuser" \
     -Dcomptafx.db.password="monmotdepasse" \
     -jar ComptaFX.jar
```

---

## 📦 Technologies

<div align="center">

| Couche | Technologie | Version | Rôle |
|--------|------------|---------|------|
| **UI** | JavaFX | 21 | Interface graphique FXML + CSS |
| **Langage** | Java | 17 LTS | Logique applicative, Streams, Lambdas |
| **Build** | Apache Maven | 3.11 | Gestion dépendances & build lifecycle |
| **Base de données** | MySQL | 8.2 | Persistance relationnelle |
| **JDBC** | MySQL Connector/J | 8.2.0 | Pilote JDBC MySQL |
| **Export** | Apache POI | 5.2.5 | Export Excel (`.xlsx`) |
| **Export** | iText 7 | 7.2.5 | Génération de PDF |
| **Import/Export** | OpenCSV | 5.9 | Lecture / écriture CSV |
| **IA** | Ollama API | Latest | Assistant IA local (HTTP JSON) |
| **Sérialisation** | Gson | 2.10.1 | JSON (réponses Ollama) |
| **HTTP** | java.net.http | JDK 17 | Client HTTP natif (appels Ollama) |
| **Préférences** | java.util.prefs | JDK 17 | Persistance des paramètres utilisateur |
| **Module System** | JPMS | JDK 17 | `module-info.java` |

</div>

---

## 📁 Structure du Projet

```
ComptaFX/
│
├── 📄 pom.xml                              ← Configuration Maven
├── 📄 README.md                            ← Ce fichier
│
└── src/main/
    ├── java/
    │   ├── module-info.java                ← Déclaration du module JPMS
    │   └── com/comptafx/
    │       │
    │       ├── 🚀 ApplicationPrincipale.java  ← Point d'entrée (main)
    │       │
    │       ├── entities/                   ← Modèles de données (POJOs)
    │       │   ├── Invoice.java            ← Facture
    │       │   ├── InvoiceLine.java        ← Ligne de facture
    │       │   ├── InvoiceStatus.java      ← Enum statuts facture
    │       │   ├── InvoiceType.java        ← Enum type (créance/dette)
    │       │   ├── JournalEntry.java       ← Écriture comptable
    │       │   ├── JournalEntryLine.java   ← Ligne d'écriture
    │       │   ├── JournalEntryStatus.java ← Enum statuts écriture
    │       │   ├── Client.java             ← Client ✨
    │       │   ├── TypeClient.java         ← Enum Particulier/Entreprise ✨
    │       │   ├── Fournisseur.java        ← Fournisseur ✨
    │       │   ├── CompteBancaire.java     ← Compte bancaire ✨
    │       │   └── TransactionBancaire.java ← Transaction bancaire ✨
    │       │       TypeTransaction.java    ← Enum types de paiement ✨
    │       │
    │       ├── dao/                        ← Accès aux données (JDBC)
    │       │   ├── DatabaseConfig.java     ← Connexion & initialisation BDD
    │       │   ├── DatabaseException.java  ← Exception JDBC custom
    │       │   ├── IInvoiceDAO.java        ← Interface DAO factures
    │       │   ├── InvoiceDAO.java         ← Implémentation JDBC
    │       │   ├── IJournalEntryDAO.java   ← Interface DAO écritures
    │       │   ├── JournalEntryDAO.java    ← Implémentation JDBC
    │       │   ├── IClientDAO.java         ← Interface DAO clients ✨
    │       │   ├── ClientDAO.java          ← Implémentation JDBC ✨
    │       │   ├── IFournisseurDAO.java    ← Interface DAO fournisseurs ✨
    │       │   ├── FournisseurDAO.java     ← Implémentation JDBC ✨
    │       │   ├── ICompteBancaireDAO.java ← Interface DAO trésorerie ✨
    │       │   └── CompteBancaireDAO.java  ← Implémentation JDBC ✨
    │       │
    │       ├── metier/                     ← Logique métier (Services)
    │       │   ├── Auditable.java          ← Interface audit (créé/modifié par)
    │       │   ├── ComptaException.java    ← Exception métier racine
    │       │   ├── ServiceFactures.java    ← Interface service factures
    │       │   ├── ServiceFacturesImpl.java
    │       │   ├── ServiceEcritures.java   ← Interface service écritures
    │       │   ├── ServiceEcrituresImpl.java
    │       │   ├── ServiceClients.java     ← Interface service clients ✨
    │       │   ├── ServiceClientsImpl.java ✨
    │       │   ├── ServiceFournisseurs.java ✨
    │       │   ├── ServiceFournisseursImpl.java ✨
    │       │   ├── ServiceTresorerie.java  ✨
    │       │   └── ServiceTresorerieImpl.java ✨
    │       │
    │       ├── presentation/               ← Contrôleurs JavaFX
    │       │   ├── MainController.java     ← Navigation principale
    │       │   ├── ChatController.java     ← Panneau IA
    │       │   ├── ControleurTableauBord.java
    │       │   ├── ControleurEcritures.java
    │       │   ├── ControleurFactures.java
    │       │   ├── ControleurPlanComptable.java
    │       │   ├── ControleurClients.java  ✨
    │       │   ├── ControleurFournisseurs.java ✨
    │       │   ├── ControleurTresorerie.java ✨
    │       │   ├── ControleurRapports.java ✨
    │       │   └── ControleurParametres.java ✨
    │       │
    │       └── ai/                         ← Intégration IA
    │           ├── OllamaService.java      ← Client HTTP Ollama
    │           └── CodeAccessService.java  ← Contexte code pour l'IA
    │
    └── resources/
        ├── fxml/                           ← Vues FXML (déclaratives)
        │   ├── VuePrincipale.fxml          ← Layout principal (BorderPane)
        │   ├── VueTableauBord.fxml         ← Tableau de bord
        │   ├── VueEcritures.fxml           ← Écritures comptables
        │   ├── VueFactures.fxml            ← Factures
        │   ├── VuePlanComptable.fxml       ← Plan comptable
        │   ├── ChatPanel.fxml              ← Panneau assistant IA
        │   ├── VueClients.fxml             ✨
        │   ├── VueFournisseurs.fxml        ✨
        │   ├── VueTresorerie.fxml          ✨
        │   ├── VueRapports.fxml            ✨
        │   └── VueParametres.fxml          ✨
        │
        └── css/
            └── styles.css                  ← Design system complet (749 lignes)
```

> ✨ = Ajouté dans la version 1.1

---

## 🤖 Assistant IA

ComptaFX intègre un assistant comptable intelligent accessible depuis le panneau droit de l'application.

### Fonctionnement

```
┌─────────────────────────────────────────────┐
│  Utilisateur saisit une question comptable   │
└──────────────────────┬──────────────────────┘
                       │
                       ▼
         ┌─────────────────────────┐
         │  OllamaService.java     │
         │  POST /api/generate     │
         │  model: llama3.2        │
         └────────────┬────────────┘
                      │
          ┌───────────┴───────────┐
          │                       │
    ✅ Ollama disponible    ❌ Ollama absent
    (localhost:11434)       (cloud fallback)
          │                       │
          ▼                       ▼
   Réponse locale          Réponse générique
   (privée, rapide)        (mode dégradé)
```

### Exemples de questions

```
"Comment saisir une écriture de TVA collectée ?"
"Quelle est la différence entre créance et dette ?"
"Comment enregistrer le paiement d'une facture ?"
"Explique-moi le plan comptable tunisien classe 1"
"Comment calculer la balance de vérification ?"
```

---

## ⌨️ Raccourcis Clavier

| Raccourci | Action |
|-----------|--------|
| `F5` | Actualiser la vue courante |
| `F1` | Ouvrir l'aide / documentation |
| `F11` | Basculer en plein écran |
| `Ctrl + +` | Zoom avant |
| `Ctrl + -` | Zoom arrière |
| `Ctrl + 0` | Zoom normal (100%) |

---

## 🎨 Design System

L'application utilise un **thème bleu professionnel** cohérent, défini via des variables CSS JavaFX :

```css
/* Couleurs principales */
-primary-color:   #1e3a5f   /* Bleu marine — sidebar, menu bar */
-accent-color:    #4a90d9   /* Bleu clair — boutons, focus */

/* Couleurs sémantiques */
-success-color:   #2e7d32   /* Vert — statut payé, solde positif */
-warning-color:   #f57c00   /* Orange — en attente, avertissement */
-danger-color:    #c62828   /* Rouge — retard, suppression */

/* Neutrals */
-bg-primary:      #f5f7fa   /* Fond principal */
-bg-secondary:    #ffffff   /* Cartes, tableaux */
-text-primary:    #2c3e50   /* Texte principal */
```

**Composants disponibles** : `primary-btn`, `secondary-btn`, `danger-btn`, `success-btn`, `kpi-card`, `data-table`, `page-title`, `table-card`, `menu-btn`, `toolbar-btn`, `status-bar`, `page-status-bar`.

---

## 🗺️ Feuille de Route

### Version 1.1 *(actuelle)*
- [x] Gestion des Clients
- [x] Gestion des Fournisseurs
- [x] Module Trésorerie & Comptes Bancaires
- [x] Rapports Financiers (balance, factures, tiers)
- [x] Page Paramètres avec persistance

### Version 1.2 *(planifiée)*
- [ ] Export PDF des rapports financiers
- [ ] Export Excel des tableaux (clients, factures, balance)
- [ ] Impression de factures (iText 7)
- [ ] Gestion multi-exercices fiscaux
- [ ] Tableau de bord enrichi (graphiques JavaFX Charts)

### Version 2.0 *(futur)*
- [ ] Mode multi-utilisateurs avec authentification
- [ ] Gestion des droits et profils
- [ ] Sauvegarde / restauration de la base de données
- [ ] Synchronisation cloud (optionnelle)
- [ ] Mode sombre (Dark Theme)

---

## 🤝 Contribution

Les contributions sont les bienvenues ! Voici comment participer :

```bash
# 1. Fork le dépôt
# 2. Créer une branche feature
git checkout -b feature/ma-fonctionnalite

# 3. Committer les changements
git commit -m "feat: ajouter la fonctionnalité X"

# 4. Pousser la branche
git push origin feature/ma-fonctionnalite

# 5. Ouvrir une Pull Request
```

### Standards de code

- **Langue** : commentaires et messages en **français**
- **Nommage** : classes en PascalCase, méthodes en camelCase, variables en camelCase
- **Architecture** : respecter la séparation DAO / Métier / Présentation
- **Exceptions** : toujours propager via `ComptaException` dans la couche métier
- **JavaFX** : toute modification UI doit passer par `Platform.runLater()` si hors thread JavaFX

---

## 📝 Licence

```
MIT License

Copyright (c) 2026 Mohamed Houij

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
```

---

## 👤 Auteur

<div align="center">

**Mohamed Houij**

[![GitHub](https://img.shields.io/badge/GitHub-MohamedHouij03-181717?style=flat-square&logo=github)](https://github.com/MohamedHouij03)
[![Email](https://img.shields.io/badge/Email-mohamed.houij700%40gmail.com-EA4335?style=flat-square&logo=gmail&logoColor=white)](mailto:mohamed.houij700@gmail.com)

*Développé avec ❤️ en Java · Tunis, Tunisie*

</div>

---

<div align="center">

[![Java](https://img.shields.io/badge/Java-17-ED8B00?style=flat-square&logo=openjdk&logoColor=white)](https://openjdk.org/)
[![JavaFX](https://img.shields.io/badge/JavaFX-21-4a90d9?style=flat-square&logo=java&logoColor=white)](https://openjfx.io/)
[![MySQL](https://img.shields.io/badge/MySQL-8.2-4479A1?style=flat-square&logo=mysql&logoColor=white)](https://www.mysql.com/)
[![Maven](https://img.shields.io/badge/Maven-3.11-C71A36?style=flat-square&logo=apachemaven&logoColor=white)](https://maven.apache.org/)

*⭐ Si ce projet vous a été utile, n'hésitez pas à laisser une étoile !*

</div>
