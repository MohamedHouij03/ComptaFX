package com.comptafx.presentation;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.*;

import java.net.URL;
import java.time.LocalDate;
import java.util.*;

/**
 * Contrôleur principal de l'application comptable
 * Démontre l'intégration JavaFX avec les services utilisant Lambda expressions et Streams
 */
public class MainController implements Initializable {
    
    @FXML private BorderPane mainContainer;
    @FXML private VBox sideMenu;
    @FXML private StackPane contentArea;
    @FXML private Label statusLabel;
    @FXML private Label clientLabel;
    @FXML private Label dateLabel;
    @FXML private Button maximizeBtn;
    @FXML private MenuBar menuBar;
    @FXML private MenuItem refreshMenuItem;
    @FXML private MenuItem zoomInMenuItem;
    @FXML private MenuItem zoomOutMenuItem;
    @FXML private MenuItem zoomResetMenuItem;
    @FXML private MenuItem fullScreenMenuItem;
    @FXML private MenuItem helpMenuItem;
    @FXML private SplitPane mainSplitPane;
    @FXML private VBox chatPanelContainer;
    
    private ChatController chatPanelController;
    private boolean isMaximized = false;
    private String currentViewPath = null;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize with Lambda
        javafx.application.Platform.runLater(() -> {
            setupMenuActions();
            setupKeyboardShortcuts();
            initializeDefaultData();
            loadChatPanel();
        });
        
        dateLabel.setText(LocalDate.now().toString());
        clientLabel.setText(ControleurParametres.getRaisonSociale());
    }
    
    private void loadChatPanel() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ChatPanel.fxml"));
            Parent chatPanel = loader.load();
            chatPanelController = loader.getController();
            
            // Configurer l'action de toggle
            if (chatPanelController != null) {
                chatPanelController.setToggleAction(() -> {
                    if (chatPanelController.isCollapsed()) {
                        mainSplitPane.setDividerPositions(0.75);
                        chatPanelController.setCollapsed(false);
                    } else {
                        mainSplitPane.setDividerPositions(1.0);
                        chatPanelController.setCollapsed(true);
                    }
                });
            }
            
            chatPanelContainer.getChildren().add(chatPanel);
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement du panneau de chat: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void setupMenuActions() {
        // Menu actions are set up in FXML
    }
    
    private void setupKeyboardShortcuts() {
        // Configurer les raccourcis clavier pour les menus
        if (refreshMenuItem != null) {
            refreshMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.F5));
        }
        if (zoomInMenuItem != null) {
            zoomInMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.EQUALS, KeyCombination.SHORTCUT_DOWN));
        }
        if (zoomOutMenuItem != null) {
            zoomOutMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.MINUS, KeyCombination.SHORTCUT_DOWN));
        }
        if (zoomResetMenuItem != null) {
            zoomResetMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.DIGIT0, KeyCombination.SHORTCUT_DOWN));
        }
        if (fullScreenMenuItem != null) {
            fullScreenMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.F11));
        }
        if (helpMenuItem != null) {
            helpMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.F1));
        }
    }
    
    private void initializeDefaultData() {
        // Account initialization removed - Account module deleted
            statusLabel.setText("Système initialisé avec succès");
    }
    
    @FXML
    private void showDashboard() {
        currentViewPath = "/fxml/VueTableauBord.fxml";
        loadContent(currentViewPath);
    }
    
    @FXML
    private void showJournalEntries() {
        currentViewPath = "/fxml/VueEcritures.fxml";
        loadContent(currentViewPath);
    }
    
    @FXML
    private void showInvoices() {
        currentViewPath = "/fxml/VueFactures.fxml";
        loadContent(currentViewPath);
    }
    
    @FXML
    private void showPlanComptable() {
        currentViewPath = "/fxml/VuePlanComptable.fxml";
        loadContent(currentViewPath);
    }

    @FXML
    private void showClients() {
        currentViewPath = "/fxml/VueClients.fxml";
        loadContent(currentViewPath);
        statusLabel.setText("Gestion des Clients");
    }

    @FXML
    private void showFournisseurs() {
        currentViewPath = "/fxml/VueFournisseurs.fxml";
        loadContent(currentViewPath);
        statusLabel.setText("Gestion des Fournisseurs");
    }

    @FXML
    private void showTresorerie() {
        currentViewPath = "/fxml/VueTresorerie.fxml";
        loadContent(currentViewPath);
        statusLabel.setText("Trésorerie & Comptes Bancaires");
    }

    @FXML
    private void showRapports() {
        currentViewPath = "/fxml/VueRapports.fxml";
        loadContent(currentViewPath);
        statusLabel.setText("Rapports Financiers");
    }

    @FXML
    private void showParametres() {
        currentViewPath = "/fxml/VueParametres.fxml";
        loadContent(currentViewPath);
        statusLabel.setText("Paramètres de l'Application");
    }
    
    
    private void loadContent(String fxmlPath) {
        try {
            URL resource = getClass().getResource(fxmlPath);
            if (resource != null) {
                Parent content = FXMLLoader.load(resource);
                contentArea.getChildren().clear();
                contentArea.getChildren().add(content);
            } else {
                showPlaceholder("View coming soon: " + fxmlPath);
            }
        } catch (Exception e) {
            showPlaceholder("Error loading view: " + e.getMessage());
        }
    }
    
    private void showPlaceholder(String message) {
        Label placeholder = new Label(message);
        placeholder.setStyle("-fx-font-size: 18px; -fx-text-fill: #666;");
        contentArea.getChildren().clear();
        contentArea.getChildren().add(placeholder);
    }
    
    @FXML
    private void handleNewEntry() {
        // Open new journal entry dialog
        showAlert("Nouvelle Écriture", "La fenêtre de création d'écriture s'ouvrira ici.");
    }
    
    @FXML
    private void handleNewInvoice() {
        // Open new invoice dialog
        showAlert("Nouvelle Facture", "La fenêtre de création de facture s'ouvrira ici.");
    }
    
    @FXML
    private void handleExport() {
        showAlert("Exporter", "Fonctionnalité d'exportation à venir.");
    }
    
    @FXML
    private void handleExit() {
        javafx.application.Platform.exit();
    }
    
    @FXML
    private void handleMinimize() {
        javafx.stage.Stage stage = (javafx.stage.Stage) mainContainer.getScene().getWindow();
        stage.setIconified(true);
    }
    
    @FXML
    private void handleMaximize() {
        javafx.stage.Stage stage = (javafx.stage.Stage) mainContainer.getScene().getWindow();
        if (isMaximized) {
            stage.setMaximized(false);
            maximizeBtn.setText("🗖");
            isMaximized = false;
        } else {
            stage.setMaximized(true);
            maximizeBtn.setText("🗗");
            isMaximized = true;
        }
    }
    
    @FXML
    private void handleClose() {
        javafx.stage.Stage stage = (javafx.stage.Stage) mainContainer.getScene().getWindow();
        stage.close();
    }
    
    @FXML
    private void handleAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("À Propos de ComptaFX");
        alert.setHeaderText("ComptaFX - Système de Gestion Comptable");
        alert.setContentText("""
            Version: 1.0
            Devise: Dinar Tunisien (TND)
            
            Application comptable simplifiée démontrant:
            • CRUD (Create, Read, Update, Delete)
            • Java Streams et Lambda expressions
            • Collections Java (List, Map, Set)
            • Gestion d'exceptions
            • Architecture en couches (DAO, Metier, Presentation)
            
            Développé avec JavaFX et MySQL
            """);
        alert.showAndWait();
    }
    
    @FXML
    private void handleRefresh() {
        // Recharger le contenu actuel
        if (currentViewPath != null) {
            loadContent(currentViewPath);
            statusLabel.setText("Contenu actualisé");
        } else {
            // Si aucune vue n'est chargée, charger le tableau de bord par défaut
            showDashboard();
            statusLabel.setText("Tableau de bord actualisé");
        }
    }
    
    @FXML
    private void handleZoomIn() {
        double currentScale = mainContainer.getScaleX();
        if (currentScale < 2.0) {
            double newScale = Math.min(currentScale + 0.1, 2.0);
            mainContainer.setScaleX(newScale);
            mainContainer.setScaleY(newScale);
            statusLabel.setText(String.format("Zoom: %.0f%%", newScale * 100));
        }
    }
    
    @FXML
    private void handleZoomOut() {
        double currentScale = mainContainer.getScaleX();
        if (currentScale > 0.5) {
            double newScale = Math.max(currentScale - 0.1, 0.5);
            mainContainer.setScaleX(newScale);
            mainContainer.setScaleY(newScale);
            statusLabel.setText(String.format("Zoom: %.0f%%", newScale * 100));
        }
    }
    
    @FXML
    private void handleZoomReset() {
        mainContainer.setScaleX(1.0);
        mainContainer.setScaleY(1.0);
        statusLabel.setText("Zoom réinitialisé");
    }
    
    @FXML
    private void handleFullScreen() {
        javafx.stage.Stage stage = (javafx.stage.Stage) mainContainer.getScene().getWindow();
        stage.setFullScreen(!stage.isFullScreen());
        statusLabel.setText(stage.isFullScreen() ? "Mode plein écran activé" : "Mode plein écran désactivé");
    }
    
    @FXML
    private void handleHelp() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Documentation");
        alert.setHeaderText("Aide - ComptaFX");
        alert.setContentText("""
            Bienvenue dans ComptaFX !
            
            Cette application vous permet de gérer votre comptabilité :

            📊 Tableau de Bord
            Visualisez un aperçu de vos finances avec les créances et dettes.

            📝 Écritures Comptables
            Créez et gérez vos écritures comptables avec leurs lignes de débit/crédit.

            📄 Factures
            Gérez vos factures clients (créances) et fournisseurs (dettes).

            👤 Clients — Gérez vos clients et leur situation financière.

            🏢 Fournisseurs — Gérez vos fournisseurs et vos achats.

            🏦 Trésorerie — Comptes bancaires et transactions.

            📈 Rapports — Balance de vérification, synthèse factures, situation tiers.

            ⚙ Paramètres — Configurez l'application (société, devise, base de données).

            Utilisez les raccourcis clavier pour une navigation plus rapide.
            """);
        alert.setWidth(500);
        alert.showAndWait();
    }
    
    @FXML
    private void handleKeyboardShortcuts() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Raccourcis Clavier");
        alert.setHeaderText("Raccourcis Clavier - ComptaFX");
        alert.setContentText("""
            RACCOURCIS GLOBAUX:
            
            Affichage:
            • F5          - Actualiser
            • Ctrl + +    - Zoom avant
            • Ctrl + -    - Zoom arrière
            • Ctrl + 0    - Zoom normal
            • F11         - Plein écran
            
            Aide:
            • F1          - Documentation
            
            NAVIGATION:
            Utilisez les boutons de la barre d'outils ou le menu latéral
            pour naviguer entre les différentes sections.
            """);
        alert.setWidth(400);
        alert.showAndWait();
    }
    
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

