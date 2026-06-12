package com.comptafx.presentation;

import com.comptafx.metier.ComptaException;
import com.comptafx.entities.*;
import com.comptafx.metier.ServiceFacturesImpl;
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
 * Contrôleur pour la gestion des Factures
 */
public class ControleurFactures implements Initializable {
    
    @FXML private TableView<Invoice> invoicesTable;
    @FXML private TableColumn<Invoice, String> invoiceNumberColumn;
    @FXML private TableColumn<Invoice, String> typeColumn;
    @FXML private TableColumn<Invoice, String> customerColumn;
    @FXML private TableColumn<Invoice, LocalDate> dateColumn;
    @FXML private TableColumn<Invoice, LocalDate> dueDateColumn;
    @FXML private TableColumn<Invoice, String> totalColumn;
    @FXML private TableColumn<Invoice, String> statusColumn;
    
    @FXML private TextField searchField;
    @FXML private Label statusLabel;
    
    private final ServiceFacturesImpl invoiceService = new ServiceFacturesImpl();
    private ObservableList<Invoice> invoicesList = FXCollections.observableArrayList();
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableColumns();
        setupFilters();
        loadInvoices();
    }
    
    private void setupTableColumns() {
        invoiceNumberColumn.setCellValueFactory(new PropertyValueFactory<>("invoiceNumber"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("invoiceDate"));
        dueDateColumn.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        
        typeColumn.setCellValueFactory(cellData -> 
                new SimpleStringProperty(cellData.getValue().getType().getDisplayName()));
        
        customerColumn.setCellValueFactory(cellData -> {
            Invoice inv = cellData.getValue();
            String name = inv.getType() == InvoiceType.RECEIVABLE ? 
                    inv.getCustomerName() : inv.getVendorName();
            return new SimpleStringProperty(name != null ? name : "");
        });
        
        totalColumn.setCellValueFactory(cellData -> 
                new SimpleStringProperty(String.format("%,.3f TND", cellData.getValue().getTotalAmount())));
        
        statusColumn.setCellValueFactory(cellData -> 
                new SimpleStringProperty(cellData.getValue().getStatus().getDisplayName()));
        
        // Row styling based on status and overdue
        invoicesTable.setRowFactory(tv -> {
            TableRow<Invoice> row = new TableRow<>();
            row.itemProperty().addListener((obs, oldItem, newItem) -> {
                if (newItem != null) {
                    if (newItem.isOverdue()) {
                        row.setStyle("-fx-background-color: #ffebee;");
                    } else if (newItem.getStatus() == InvoiceStatus.PAID) {
                        row.setStyle("-fx-background-color: #e8f5e9;");
                    } else {
                        row.setStyle("");
                    }
                } else {
                    row.setStyle("");
                }
            });
            return row;
        });
    }
    
    private void setupFilters() {
        // Simple search filter using Lambda
        searchField.textProperty().addListener((obs, old, newVal) -> filterInvoices());
    }
    
    private void loadInvoices() {
        System.out.println("loadInvoices() called");
        try {
            List<Invoice> invoices = invoiceService.getAllInvoices();
            System.out.println("Loaded " + invoices.size() + " invoices from service");
            invoicesList.clear();
            invoicesList.addAll(invoices);
            filterInvoices();
            invoicesTable.refresh();
            statusLabel.setText(invoices.size() + " factures chargées");
        } catch (ComptaException e) {
            System.err.println("Error loading invoices: " + e.getMessage());
            e.printStackTrace();
            statusLabel.setText("Erreur: " + e.getMessage());
        }
    }
    
    private void filterInvoices() {
        // Simple search using Stream and Lambda
        String searchText = searchField.getText().toLowerCase();
        
        List<Invoice> filtered = invoicesList.stream()
                .filter(inv -> searchText.isEmpty() ||
                        inv.getInvoiceNumber().toLowerCase().contains(searchText) ||
                        (inv.getCustomerName() != null && 
                         inv.getCustomerName().toLowerCase().contains(searchText)) ||
                        (inv.getVendorName() != null && 
                         inv.getVendorName().toLowerCase().contains(searchText)))
                .sorted(Comparator.comparing(Invoice::getInvoiceDate).reversed())
                .collect(Collectors.toList());
        
        invoicesTable.getItems().clear();
        invoicesTable.getItems().addAll(filtered);
        invoicesTable.refresh();
    }
    
    @FXML
    private void handleNewInvoice() {
        Dialog<Invoice> dialog = createInvoiceDialog(null);
        dialog.showAndWait().ifPresent(invoice -> {
            try {
                invoiceService.createInvoice(invoice);
                loadInvoices();
                statusLabel.setText("Facture créée: " + invoice.getInvoiceNumber());
            } catch (ComptaException e) {
                showError("Erreur de création", e.getMessage());
            }
        });
    }
    
    @FXML
    private void handleEditInvoice() {
        Invoice selected = invoicesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Aucune Sélection", "Veuillez sélectionner une facture à modifier.");
            return;
        }
        
        if (selected.getStatus() == InvoiceStatus.PAID) {
            showError("Modification impossible", "Les factures payées ne peuvent pas être modifiées.");
            return;
        }
        
        Dialog<Invoice> dialog = createInvoiceDialog(selected);
        dialog.showAndWait().ifPresent(invoice -> {
            try {
                invoiceService.updateInvoice(invoice);
                loadInvoices();
                statusLabel.setText("Facture modifiée: " + invoice.getInvoiceNumber());
            } catch (ComptaException e) {
                showError("Erreur de modification", e.getMessage());
            }
        });
    }
    
    @FXML
    private void handleDeleteInvoice() {
        Invoice selected = invoicesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Aucune Sélection", "Veuillez sélectionner une facture à supprimer.");
            return;
        }
        
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmer la Suppression");
        confirm.setHeaderText("Supprimer la Facture: " + selected.getInvoiceNumber());
        confirm.setContentText("Êtes-vous sûr de vouloir supprimer définitivement cette facture? Cette action est irréversible.");
        
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    invoiceService.deleteInvoice(selected.getId());
                    loadInvoices();
                    statusLabel.setText("Facture supprimée: " + selected.getInvoiceNumber());
                } catch (ComptaException e) {
                    showError("Erreur de suppression", e.getMessage());
                }
            }
        });
    }
    
    @FXML
    private void handleShowAgingReport() {
        try {
            Map<String, BigDecimal> aging = invoiceService.getAgingReport();
            showAgingDialog(aging);
        } catch (ComptaException e) {
            showError("Erreur de génération", e.getMessage());
        }
    }
    
    private void showAgingDialog(Map<String, BigDecimal> aging) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Échéancier");
        dialog.setHeaderText("Échéancier des Créances");
        
        VBox content = new VBox(10);
        content.setPadding(new Insets(20));
        
        // Translate aging buckets
        Map<String, String> bucketTranslations = new LinkedHashMap<>();
        bucketTranslations.put("Current", "À jour");
        bucketTranslations.put("1-30 Days", "1-30 Jours");
        bucketTranslations.put("31-60 Days", "31-60 Jours");
        bucketTranslations.put("61-90 Days", "61-90 Jours");
        bucketTranslations.put("Over 90 Days", "Plus de 90 Jours");
        
        aging.forEach((bucket, amount) -> {
            HBox row = new HBox(20);
            String frenchBucket = bucketTranslations.getOrDefault(bucket, bucket);
            Label bucketLabel = new Label(frenchBucket + ":");
            bucketLabel.setPrefWidth(120);
            Label amountLabel = new Label(String.format("%,.3f TND", amount));
            row.getChildren().addAll(bucketLabel, amountLabel);
            content.getChildren().add(row);
        });
        
        BigDecimal total = aging.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        content.getChildren().add(new Separator());
        HBox totalRow = new HBox(20);
        Label totalLabel = new Label("Total:");
        totalLabel.setStyle("-fx-font-weight: bold;");
        totalLabel.setPrefWidth(120);
        Label totalAmountLabel = new Label(String.format("%,.3f TND", total));
        totalAmountLabel.setStyle("-fx-font-weight: bold;");
        totalRow.getChildren().addAll(totalLabel, totalAmountLabel);
        content.getChildren().add(totalRow);
        
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }
    
    @SuppressWarnings("unchecked")
    private Dialog<Invoice> createInvoiceDialog(Invoice existingInvoice) {
        Dialog<Invoice> dialog = new Dialog<>();
        dialog.setTitle(existingInvoice == null ? "Nouvelle Facture" : "Modifier la Facture");
        
        ButtonType saveButtonType = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        
        VBox content = new VBox(10);
        content.setPadding(new Insets(20));
        
        GridPane header = new GridPane();
        header.setHgap(10);
        header.setVgap(10);
        
        // Type ComboBox with French display names
        ComboBox<InvoiceType> typeCombo = new ComboBox<>(
                FXCollections.observableArrayList(InvoiceType.values()));
        typeCombo.setConverter(new javafx.util.StringConverter<InvoiceType>() {
            @Override
            public String toString(InvoiceType type) {
                return type == null ? "" : type.getDisplayName();
            }
            @Override
            public InvoiceType fromString(String string) {
                return null;
            }
        });
        
        // Status ComboBox with French display names
        ComboBox<InvoiceStatus> statusCombo = new ComboBox<>(
                FXCollections.observableArrayList(InvoiceStatus.values()));
        statusCombo.setConverter(new javafx.util.StringConverter<InvoiceStatus>() {
            @Override
            public String toString(InvoiceStatus status) {
                return status == null ? "" : status.getDisplayName();
            }
            @Override
            public InvoiceStatus fromString(String string) {
                return null;
            }
        });
        
        // Client/Fournisseur text field
        Label customerLabel = new Label("Client:");
        TextField customerField = new TextField();
        customerField.setPrefWidth(200);
        customerField.setPromptText("Saisir le nom du client");
        
        // Update label when type changes
        typeCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == InvoiceType.RECEIVABLE) {
                customerLabel.setText("Client:");
                customerField.setPromptText("Saisir le nom du client");
            } else {
                customerLabel.setText("Fournisseur:");
                customerField.setPromptText("Saisir le nom du fournisseur");
            }
        });
        
        DatePicker datePicker = new DatePicker(LocalDate.now());
        DatePicker dueDatePicker = new DatePicker(LocalDate.now().plusDays(30));
        TextArea notesArea = new TextArea();
        notesArea.setPrefRowCount(2);
        
        if (existingInvoice != null) {
            typeCombo.setValue(existingInvoice.getType());
            typeCombo.setDisable(true);
            if (existingInvoice.getType() == InvoiceType.RECEIVABLE) {
                customerField.setText(existingInvoice.getCustomerName());
            } else {
                customerField.setText(existingInvoice.getVendorName());
            }
            statusCombo.setValue(existingInvoice.getStatus());
            datePicker.setValue(existingInvoice.getInvoiceDate());
            dueDatePicker.setValue(existingInvoice.getDueDate());
            notesArea.setText(existingInvoice.getNotes());
        } else {
            typeCombo.setValue(InvoiceType.RECEIVABLE);
            statusCombo.setValue(InvoiceStatus.DRAFT);
        }
        
        header.add(new Label("Type:"), 0, 0);
        header.add(typeCombo, 1, 0);
        header.add(customerLabel, 0, 1);
        header.add(customerField, 1, 1);
        header.add(new Label("Statut:"), 0, 2);
        header.add(statusCombo, 1, 2);
        header.add(new Label("Date:"), 0, 3);
        header.add(datePicker, 1, 3);
        header.add(new Label("Échéance:"), 0, 4);
        header.add(dueDatePicker, 1, 4);
        header.add(new Label("Notes:"), 0, 5);
        header.add(notesArea, 1, 5);
        
        content.getChildren().add(header);
        
        // Lines
        ObservableList<InvoiceLine> lines = FXCollections.observableArrayList();
        if (existingInvoice != null) {
            lines.addAll(existingInvoice.getLines());
        }
        
        TableView<InvoiceLine> linesTableDialog = new TableView<>(lines);
        TableColumn<InvoiceLine, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        TableColumn<InvoiceLine, String> qtyCol = new TableColumn<>("Qté");
        qtyCol.setCellValueFactory(cellData -> 
                new SimpleStringProperty(cellData.getValue().getQuantity().toString()));
        TableColumn<InvoiceLine, String> priceCol = new TableColumn<>("Prix U.");
        priceCol.setCellValueFactory(cellData -> 
                new SimpleStringProperty(String.format("%.3f", cellData.getValue().getUnitPrice())));
        TableColumn<InvoiceLine, String> totalCol = new TableColumn<>("Total");
        totalCol.setCellValueFactory(cellData -> 
                new SimpleStringProperty(String.format("%.3f", cellData.getValue().getLineTotal())));
        
        linesTableDialog.getColumns().addAll(descCol, qtyCol, priceCol, totalCol);
        linesTableDialog.setPrefHeight(150);
        
        content.getChildren().add(new Label("Lignes:"));
        content.getChildren().add(linesTableDialog);
        
        // Add line controls
        HBox addLineBox = new HBox(10);
        TextField lineDescField = new TextField();
        lineDescField.setPromptText("Description");
        TextField qtyField = new TextField("1");
        qtyField.setPrefWidth(60);
        TextField priceField = new TextField("0");
        priceField.setPrefWidth(80);
        TextField taxField = new TextField("19");
        taxField.setPrefWidth(60);
        Button addLineBtn = new Button("Ajouter");
        
        addLineBtn.setOnAction(e -> {
            InvoiceLine line = new InvoiceLine(
                    lineDescField.getText(),
                    new BigDecimal(qtyField.getText()),
                    new BigDecimal(priceField.getText()),
                    new BigDecimal(taxField.getText())
            );
            lines.add(line);
            lineDescField.clear();
            qtyField.setText("1");
            priceField.setText("0");
        });
        
        addLineBox.getChildren().addAll(
                lineDescField, 
                new Label("Qté:"), qtyField,
                new Label("Prix:"), priceField,
                new Label("TVA %:"), taxField,
                addLineBtn
        );
        content.getChildren().add(addLineBox);
        
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().setPrefSize(600, 500);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                Invoice invoice = existingInvoice != null ? existingInvoice : new Invoice();
                invoice.setType(typeCombo.getValue());
                invoice.setStatus(statusCombo.getValue());
                invoice.setInvoiceDate(datePicker.getValue());
                invoice.setDueDate(dueDatePicker.getValue());
                invoice.setNotes(notesArea.getText());
                
                String enteredName = customerField.getText();
                if (typeCombo.getValue() == InvoiceType.RECEIVABLE) {
                    invoice.setCustomerName(enteredName);
                } else {
                    invoice.setVendorName(enteredName);
                }
                
                invoice.getLines().clear();
                lines.forEach(invoice::addLine);
                
                return invoice;
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
