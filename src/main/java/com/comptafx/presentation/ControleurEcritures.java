package com.comptafx.presentation;

import com.comptafx.metier.ComptaException;
import com.comptafx.entities.*;
import com.comptafx.metier.ServiceEcrituresImpl;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Contrôleur pour la gestion des Écritures Comptables
 */
public class ControleurEcritures implements Initializable {
    
    @FXML private TableView<JournalEntry> entriesTable;
    @FXML private TableColumn<JournalEntry, String> entryNumberColumn;
    @FXML private TableColumn<JournalEntry, LocalDate> dateColumn;
    @FXML private TableColumn<JournalEntry, String> descriptionColumn;
    @FXML private TableColumn<JournalEntry, String> debitColumn;
    @FXML private TableColumn<JournalEntry, String> creditColumn;
    @FXML private TableColumn<JournalEntry, String> statusColumn;
    
    @FXML private Label statusLabel;
    @FXML private Label totalDebitsLabel;
    @FXML private Label totalCreditsLabel;
    
    @FXML private TableView<JournalEntryLine> linesTable;
    @FXML private TableColumn<JournalEntryLine, String> lineAccountColumn;
    @FXML private TableColumn<JournalEntryLine, String> lineDescColumn;
    @FXML private TableColumn<JournalEntryLine, String> lineDebitColumn;
    @FXML private TableColumn<JournalEntryLine, String> lineCreditColumn;
    
    private final ServiceEcrituresImpl journalService = new ServiceEcrituresImpl();
    private ObservableList<JournalEntry> entriesList = FXCollections.observableArrayList();
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableColumns();
        setupFilters();
        setupSelectionListener();
        loadEntries();
    }
    
    private void setupTableColumns() {
        entryNumberColumn.setCellValueFactory(new PropertyValueFactory<>("entryNumber"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("entryDate"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        
        // Using Lambda for formatted values
        debitColumn.setCellValueFactory(cellData -> 
                new SimpleStringProperty(String.format("%,.3f TND", cellData.getValue().getTotalDebit())));
        
        creditColumn.setCellValueFactory(cellData -> 
                new SimpleStringProperty(String.format("%,.3f TND", cellData.getValue().getTotalCredit())));
        
        statusColumn.setCellValueFactory(cellData -> 
                new SimpleStringProperty(cellData.getValue().getStatus().getDisplayName()));
        
        // Lines table columns
        lineAccountColumn.setCellValueFactory(new PropertyValueFactory<>("accountCode"));
        lineDescColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        
        lineDebitColumn.setCellValueFactory(cellData -> 
                new SimpleStringProperty(String.format("%,.3f TND", cellData.getValue().getDebit())));
        
        lineCreditColumn.setCellValueFactory(cellData -> 
                new SimpleStringProperty(String.format("%,.3f TND", cellData.getValue().getCredit())));
        
        // Row styling based on status
        entriesTable.setRowFactory(tv -> {
            TableRow<JournalEntry> row = new TableRow<>();
            row.itemProperty().addListener((obs, oldItem, newItem) -> {
                if (newItem != null) {
                    switch (newItem.getStatus()) {
                        case POSTED -> row.setStyle("-fx-background-color: #e8f5e9;");
                        case REVERSED -> row.setStyle("-fx-background-color: #fff3e0;");
                        case CANCELLED -> row.setStyle("-fx-background-color: #ffebee;");
                        default -> row.setStyle("");
                    }
                } else {
                    row.setStyle("");
                }
            });
            return row;
        });
    }
    
    private void setupFilters() {
    }
    
    private void setupSelectionListener() {
        // Show entry lines when entry is selected
        entriesTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                linesTable.setItems(FXCollections.observableArrayList(newSel.getLines()));
            } else {
                linesTable.getItems().clear();
            }
        });
    }
    
    private void loadEntries() {
        try {
            List<JournalEntry> entries = journalService.getAllEntries();
            entriesList.setAll(entries);
            
            // Sort using Stream and Lambda
            List<JournalEntry> sorted = entriesList.stream()
                    .sorted(Comparator.comparing(JournalEntry::getEntryDate).reversed())
                    .collect(Collectors.toList());
            
            entriesTable.setItems(FXCollections.observableArrayList(sorted));
            updateTotals();
            statusLabel.setText(entries.size() + " écritures chargées");
        } catch (ComptaException e) {
            statusLabel.setText("Erreur: " + e.getMessage());
        }
    }
    
    private void updateTotals() {
        // Calculate totals using Stream
        BigDecimal totalDebits = entriesTable.getItems().stream()
                .map(JournalEntry::getTotalDebit)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalCredits = entriesTable.getItems().stream()
                .map(JournalEntry::getTotalCredit)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        totalDebitsLabel.setText(String.format("%,.3f TND", totalDebits));
        totalCreditsLabel.setText(String.format("%,.3f TND", totalCredits));
    }
    
    @FXML
    private void handleNewEntry() {
        Dialog<JournalEntry> dialog = createEntryDialog(null);
        dialog.showAndWait().ifPresent(entry -> {
            try {
                journalService.createEntry(entry);
                loadEntries();
                statusLabel.setText("Écriture créée: " + entry.getEntryNumber());
            } catch (ComptaException e) {
                showError("Erreur de création", e.getMessage());
            }
        });
    }
    
    @FXML
    private void handleDeleteEntry() {
        System.out.println("handleDeleteEntry() called");
        JournalEntry selected = entriesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            System.out.println("No entry selected");
            showError("Aucune Sélection", "Veuillez sélectionner une écriture à supprimer.");
            return;
        }
        
        System.out.println("Selected entry: " + selected.getEntryNumber() + " ID: " + selected.getId() + " Status: " + selected.getStatus());
        
        if (selected.getId() == null) {
            System.out.println("Entry ID is null");
            showError("Erreur", "L'écriture n'a pas d'ID valide.");
            return;
        }
        
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmer la Suppression");
        confirm.setHeaderText("Supprimer l'Écriture: " + selected.getEntryNumber());
        String statusWarning = selected.getStatus() != JournalEntryStatus.DRAFT ? 
            "\n\n⚠️ Attention: Cette écriture est " + selected.getStatus().getDisplayName() + ". La supprimer peut affecter votre comptabilité." : "";
        confirm.setContentText("Êtes-vous sûr de vouloir supprimer définitivement cette écriture?" + statusWarning + "\n\nCette action est irréversible.");
        
        confirm.showAndWait().ifPresent(response -> {
            System.out.println("Dialog response: " + response);
            if (response == ButtonType.OK) {
                try {
                    System.out.println("Deleting entry ID: " + selected.getId());
                    journalService.deleteEntry(selected.getId());
                    System.out.println("Entry deleted successfully");
                    loadEntries();
                    statusLabel.setText("Écriture supprimée: " + selected.getEntryNumber());
                } catch (ComptaException e) {
                    System.err.println("Error deleting entry: " + e.getMessage());
                    e.printStackTrace();
                    showError("Erreur de suppression", e.getMessage());
                }
            }
        });
    }
    
    @FXML
    private void handleEditEntry() {
        JournalEntry selected = entriesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Aucune Sélection", "Veuillez sélectionner une écriture à modifier.");
            return;
        }
        
        Dialog<JournalEntry> dialog = createEntryDialog(selected);
        dialog.showAndWait().ifPresent(entry -> {
            try {
                journalService.updateEntry(entry);
                loadEntries();
                statusLabel.setText("Écriture modifiée: " + entry.getEntryNumber());
            } catch (ComptaException e) {
                showError("Erreur de modification", e.getMessage());
            }
        });
    }
    
    @SuppressWarnings("unchecked")
    private Dialog<JournalEntry> createEntryDialog(JournalEntry existingEntry) {
        Dialog<JournalEntry> dialog = new Dialog<>();
        boolean isEdit = existingEntry != null;
        dialog.setTitle(isEdit ? "Modifier Écriture Comptable" : "Nouvelle Écriture Comptable");
        dialog.setHeaderText(isEdit ? "Modifier l'écriture comptable" : "Créer une nouvelle écriture comptable");
        
        ButtonType saveButtonType = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        
        VBox content = new VBox(10);
        content.setPadding(new Insets(20));
        
        // Entry header fields
        GridPane header = new GridPane();
        header.setHgap(10);
        header.setVgap(10);
        
        DatePicker datePicker = new DatePicker(isEdit ? existingEntry.getEntryDate() : LocalDate.now());
        TextField descField = new TextField(isEdit ? existingEntry.getDescription() : "");
        TextField refField = new TextField(isEdit ? existingEntry.getReference() : "");
        
        header.add(new Label("Date:"), 0, 0);
        header.add(datePicker, 1, 0);
        header.add(new Label("Description:"), 0, 1);
        header.add(descField, 1, 1);
        header.add(new Label("Référence:"), 0, 2);
        header.add(refField, 1, 2);
        
        content.getChildren().add(header);
        
        // Lines table
        TableView<JournalEntryLine> linesTableDialog = new TableView<>();
        ObservableList<JournalEntryLine> lines = FXCollections.observableArrayList();
        if (isEdit && existingEntry.getLines() != null) {
            lines.addAll(existingEntry.getLines());
        }
        linesTableDialog.setItems(lines);
        
        TableColumn<JournalEntryLine, String> accCol = new TableColumn<>("Compte");
        accCol.setCellValueFactory(new PropertyValueFactory<>("accountCode"));
        accCol.setPrefWidth(150);
        
        TableColumn<JournalEntryLine, String> debCol = new TableColumn<>("Débit");
        debCol.setCellValueFactory(cellData -> 
                new SimpleStringProperty(String.format("%.3f", cellData.getValue().getDebit())));
        
        TableColumn<JournalEntryLine, String> credCol = new TableColumn<>("Crédit");
        credCol.setCellValueFactory(cellData -> 
                new SimpleStringProperty(String.format("%.3f", cellData.getValue().getCredit())));
        
        linesTableDialog.getColumns().addAll(accCol, debCol, credCol);
        linesTableDialog.setPrefHeight(200);
        
        content.getChildren().add(new Label("Lignes d'Écriture:"));
        content.getChildren().add(linesTableDialog);
        
        // Add line controls
        HBox addLineBox = new HBox(10);
        TextField accountCodeField = new TextField();
        accountCodeField.setPromptText("Code compte (ex: 1000)");
        accountCodeField.setPrefWidth(120);
        TextField debitField = new TextField("0");
        debitField.setPrefWidth(80);
        TextField creditField = new TextField("0");
        creditField.setPrefWidth(80);
        Button addLineBtn = new Button("Ajouter Ligne");
        
        addLineBtn.setOnAction(e -> {
            String accountCode = accountCodeField.getText().trim();
            if (!accountCode.isEmpty()) {
                try {
                    JournalEntryLine line = new JournalEntryLine();
                    line.setAccountCode(accountCode);
                    line.setDebit(new BigDecimal(debitField.getText()));
                    line.setCredit(new BigDecimal(creditField.getText()));
                    lines.add(line);
                    accountCodeField.clear();
                    debitField.setText("0");
                    creditField.setText("0");
                } catch (NumberFormatException ex) {
                    showError("Erreur", "Veuillez entrer des montants valides");
                }
            } else {
                showError("Erreur", "Veuillez entrer un code compte");
            }
        });
        
        addLineBox.getChildren().addAll(
                new Label("Compte:"), accountCodeField,
                new Label("Débit:"), debitField,
                new Label("Crédit:"), creditField,
                addLineBtn
        );
        content.getChildren().add(addLineBox);
        
        // Totals
        Label totalsLabel = new Label("Débit: 0,000 | Crédit: 0,000");
        lines.addListener((javafx.collections.ListChangeListener<JournalEntryLine>) c -> {
            BigDecimal totalDebit = lines.stream().map(JournalEntryLine::getDebit)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal totalCredit = lines.stream().map(JournalEntryLine::getCredit)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            totalsLabel.setText(String.format("Débit: %,.3f | Crédit: %,.3f", totalDebit, totalCredit));
        });
        content.getChildren().add(totalsLabel);
        
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().setPrefSize(600, 500);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                JournalEntry entry = isEdit ? existingEntry : new JournalEntry();
                if (isEdit) {
                    entry.setId(existingEntry.getId());
                    entry.setEntryNumber(existingEntry.getEntryNumber());
                }
                entry.setEntryDate(datePicker.getValue());
                entry.setDescription(descField.getText());
                entry.setReference(refField.getText());
                entry.getLines().clear();
                lines.forEach(entry::addLine);
                return entry;
            }
            return null;
        });
        
        return dialog;
    }
    
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
