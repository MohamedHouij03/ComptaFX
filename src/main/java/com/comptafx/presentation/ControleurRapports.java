package com.comptafx.presentation;

import com.comptafx.entities.Invoice;
import com.comptafx.entities.InvoiceStatus;
import com.comptafx.entities.InvoiceType;
import com.comptafx.entities.JournalEntry;
import com.comptafx.metier.ComptaException;
import com.comptafx.metier.ServiceEcrituresImpl;
import com.comptafx.metier.ServiceFacturesImpl;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class ControleurRapports implements Initializable {

    // Filtres période
    @FXML private DatePicker dateDebutPicker;
    @FXML private DatePicker dateFinPicker;
    @FXML private ComboBox<String> periodeRapideCombo;
    @FXML private Label statusLabel;

    // Balance
    @FXML private TableView<Map<String, String>> balanceTable;
    @FXML private TableColumn<Map<String, String>, String> balCodeCol;
    @FXML private TableColumn<Map<String, String>, String> balNomCol;
    @FXML private TableColumn<Map<String, String>, String> balDebitCol;
    @FXML private TableColumn<Map<String, String>, String> balCreditCol;
    @FXML private TableColumn<Map<String, String>, String> balSoldeCol;
    @FXML private TextField balanceSearchField;
    @FXML private Label balanceTotauxLabel;

    // Factures synthèse
    @FXML private TableView<Map<String, String>> facturesRapportTable;
    @FXML private TableColumn<Map<String, String>, String> frStatutCol;
    @FXML private TableColumn<Map<String, String>, String> frNbCol;
    @FXML private TableColumn<Map<String, String>, String> frMontantCol;
    @FXML private TableColumn<Map<String, String>, String> frPayeCol;
    @FXML private TableColumn<Map<String, String>, String> frResteCol;
    @FXML private Label creancesLabel;
    @FXML private Label dettesLabel;
    @FXML private Label retardLabel;
    @FXML private Label caLabel;

    // Clients rapport
    @FXML private TableView<Map<String, String>> clientsRapportTable;
    @FXML private TableColumn<Map<String, String>, String> crNomCol;
    @FXML private TableColumn<Map<String, String>, String> crNbFactCol;
    @FXML private TableColumn<Map<String, String>, String> crTotalCol;
    @FXML private TableColumn<Map<String, String>, String> crPayeCol;
    @FXML private TableColumn<Map<String, String>, String> crSoldeCol;
    @FXML private TableColumn<Map<String, String>, String> crEtatCol;

    // Fournisseurs rapport
    @FXML private TableView<Map<String, String>> fournisseursRapportTable;
    @FXML private TableColumn<Map<String, String>, String> frNomFCol;
    @FXML private TableColumn<Map<String, String>, String> frNbFactFCol;
    @FXML private TableColumn<Map<String, String>, String> frTotalFCol;
    @FXML private TableColumn<Map<String, String>, String> frPayeFCol;
    @FXML private TableColumn<Map<String, String>, String> frSoldeFCol;
    @FXML private TableColumn<Map<String, String>, String> frEtatFCol;

    private final ServiceFacturesImpl factureService = new ServiceFacturesImpl();
    private final ServiceEcrituresImpl ecritureService = new ServiceEcrituresImpl();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupColumns();
        setupPeriodes();
        // Load with current year by default
        dateDebutPicker.setValue(LocalDate.now().withDayOfYear(1));
        dateFinPicker.setValue(LocalDate.now());
        handleGenerer();
    }

    private void setupColumns() {
        // Balance
        balCodeCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get("code")));
        balNomCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get("nom")));
        balDebitCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get("debit")));
        balCreditCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get("credit")));
        balSoldeCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get("solde")));

        balSoldeCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item);
                if (!empty && item != null && item.startsWith("-"))
                    setStyle("-fx-text-fill: #c62828;");
                else if (!empty) setStyle("-fx-text-fill: #2e7d32;");
                else setStyle("");
            }
        });

        balanceSearchField.textProperty().addListener((obs, old, val) -> filtrerBalance());

        // Factures synthèse
        frStatutCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get("statut")));
        frNbCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get("nb")));
        frMontantCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get("montant")));
        frPayeCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get("paye")));
        frResteCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get("reste")));

        // Clients
        crNomCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get("nom")));
        crNbFactCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get("nb")));
        crTotalCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get("total")));
        crPayeCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get("paye")));
        crSoldeCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get("solde")));
        crEtatCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get("etat")));

        // Fournisseurs
        frNomFCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get("nom")));
        frNbFactFCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get("nb")));
        frTotalFCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get("total")));
        frPayeFCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get("paye")));
        frSoldeFCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get("solde")));
        frEtatFCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get("etat")));
    }

    private void setupPeriodes() {
        periodeRapideCombo.setItems(FXCollections.observableArrayList(
                "Ce mois", "Trimestre en cours", "Année en cours", "Année précédente", "Tout"));
        periodeRapideCombo.valueProperty().addListener((obs, old, val) -> {
            if (val == null) return;
            LocalDate now = LocalDate.now();
            switch (val) {
                case "Ce mois" -> { dateDebutPicker.setValue(now.withDayOfMonth(1)); dateFinPicker.setValue(now); }
                case "Trimestre en cours" -> {
                    int q = (now.getMonthValue() - 1) / 3;
                    dateDebutPicker.setValue(now.withMonth(q * 3 + 1).withDayOfMonth(1));
                    dateFinPicker.setValue(now);
                }
                case "Année en cours" -> { dateDebutPicker.setValue(now.withDayOfYear(1)); dateFinPicker.setValue(now); }
                case "Année précédente" -> {
                    dateDebutPicker.setValue(now.minusYears(1).withDayOfYear(1));
                    dateFinPicker.setValue(now.minusYears(1).withMonth(12).withDayOfMonth(31));
                }
                case "Tout" -> { dateDebutPicker.setValue(LocalDate.of(2000, 1, 1)); dateFinPicker.setValue(now); }
            }
        });
    }

    @FXML
    private void handleGenerer() {
        LocalDate debut = dateDebutPicker.getValue();
        LocalDate fin = dateFinPicker.getValue();
        if (debut == null || fin == null) {
            statusLabel.setText("Veuillez sélectionner une période.");
            return;
        }
        genererBalance(debut, fin);
        genererRapportFactures(debut, fin);
        genererRapportClients(debut, fin);
        genererRapportFournisseurs(debut, fin);
        statusLabel.setText("Rapports générés pour la période: " + debut + " → " + fin);
    }

    private List<Map<String, String>> balanceData = new ArrayList<>();

    private void genererBalance(LocalDate debut, LocalDate fin) {
        try {
            List<JournalEntry> entries = ecritureService.getAllEntries().stream()
                    .filter(e -> !e.getEntryDate().isBefore(debut) && !e.getEntryDate().isAfter(fin))
                    .collect(Collectors.toList());

            Map<String, BigDecimal[]> comptes = new TreeMap<>();
            entries.forEach(entry -> entry.getLines().forEach(line -> {
                String key = line.getAccountCode() + "|" + (line.getAccountName() != null ? line.getAccountName() : "");
                comptes.computeIfAbsent(key, k -> new BigDecimal[]{BigDecimal.ZERO, BigDecimal.ZERO});
                comptes.get(key)[0] = comptes.get(key)[0].add(line.getDebit());
                comptes.get(key)[1] = comptes.get(key)[1].add(line.getCredit());
            }));

            balanceData = comptes.entrySet().stream().map(e -> {
                String[] parts = e.getKey().split("\\|", 2);
                BigDecimal debit = e.getValue()[0];
                BigDecimal credit = e.getValue()[1];
                BigDecimal solde = debit.subtract(credit);
                Map<String, String> row = new LinkedHashMap<>();
                row.put("code", parts[0]);
                row.put("nom", parts.length > 1 ? parts[1] : "");
                row.put("debit", String.format("%,.3f", debit));
                row.put("credit", String.format("%,.3f", credit));
                row.put("solde", String.format("%,.3f", solde));
                return row;
            }).collect(Collectors.toList());

            BigDecimal totDebit = comptes.values().stream().map(v -> v[0]).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal totCredit = comptes.values().stream().map(v -> v[1]).reduce(BigDecimal.ZERO, BigDecimal::add);
            balanceTotauxLabel.setText(String.format("Total Débit: %,.3f | Total Crédit: %,.3f", totDebit, totCredit));

            filtrerBalance();
        } catch (ComptaException e) {
            statusLabel.setText("Erreur balance: " + e.getMessage());
        }
    }

    private void filtrerBalance() {
        String search = balanceSearchField.getText().toLowerCase();
        List<Map<String, String>> filtered = balanceData.stream()
                .filter(r -> search.isBlank()
                        || r.get("code").toLowerCase().contains(search)
                        || r.get("nom").toLowerCase().contains(search))
                .collect(Collectors.toList());
        balanceTable.getItems().setAll(filtered);
    }

    private void genererRapportFactures(LocalDate debut, LocalDate fin) {
        try {
            List<Invoice> all = factureService.getAllInvoices().stream()
                    .filter(inv -> !inv.getInvoiceDate().isBefore(debut) && !inv.getInvoiceDate().isAfter(fin))
                    .collect(Collectors.toList());

            // KPIs
            BigDecimal creances = all.stream()
                    .filter(i -> i.getType() == InvoiceType.RECEIVABLE
                            && i.getStatus() != InvoiceStatus.PAID && i.getStatus() != InvoiceStatus.CANCELLED)
                    .map(Invoice::getBalanceDue).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal dettes = all.stream()
                    .filter(i -> i.getType() == InvoiceType.PAYABLE
                            && i.getStatus() != InvoiceStatus.PAID && i.getStatus() != InvoiceStatus.CANCELLED)
                    .map(Invoice::getBalanceDue).reduce(BigDecimal.ZERO, BigDecimal::add);
            long enRetard = all.stream().filter(Invoice::isOverdue).count();
            BigDecimal ca = all.stream()
                    .filter(i -> i.getType() == InvoiceType.RECEIVABLE && i.getStatus() == InvoiceStatus.PAID)
                    .map(Invoice::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

            creancesLabel.setText(String.format("%,.3f TND", creances));
            dettesLabel.setText(String.format("%,.3f TND", dettes));
            retardLabel.setText(String.valueOf(enRetard));
            caLabel.setText(String.format("%,.3f TND", ca));

            // Synthèse par statut
            Map<InvoiceStatus, List<Invoice>> byStatus = all.stream()
                    .collect(Collectors.groupingBy(Invoice::getStatus));
            List<Map<String, String>> rows = new ArrayList<>();
            for (InvoiceStatus status : InvoiceStatus.values()) {
                List<Invoice> group = byStatus.getOrDefault(status, List.of());
                if (group.isEmpty()) continue;
                BigDecimal total = group.stream().map(Invoice::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal paye = group.stream().map(Invoice::getPaidAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                Map<String, String> row = new LinkedHashMap<>();
                row.put("statut", status.getDisplayName());
                row.put("nb", String.valueOf(group.size()));
                row.put("montant", String.format("%,.3f TND", total));
                row.put("paye", String.format("%,.3f TND", paye));
                row.put("reste", String.format("%,.3f TND", total.subtract(paye)));
                rows.add(row);
            }
            facturesRapportTable.getItems().setAll(rows);
        } catch (ComptaException e) {
            statusLabel.setText("Erreur factures: " + e.getMessage());
        }
    }

    private void genererRapportClients(LocalDate debut, LocalDate fin) {
        try {
            List<Invoice> receivables = factureService.getAllInvoices().stream()
                    .filter(i -> i.getType() == InvoiceType.RECEIVABLE
                            && !i.getInvoiceDate().isBefore(debut) && !i.getInvoiceDate().isAfter(fin))
                    .collect(Collectors.toList());

            Map<String, List<Invoice>> byClient = receivables.stream()
                    .collect(Collectors.groupingBy(i -> i.getCustomerName() != null ? i.getCustomerName() : "Inconnu"));

            List<Map<String, String>> rows = byClient.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .map(e -> {
                        List<Invoice> group = e.getValue();
                        BigDecimal total = group.stream().map(Invoice::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                        BigDecimal paye = group.stream().map(Invoice::getPaidAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                        BigDecimal solde = total.subtract(paye);
                        Map<String, String> row = new LinkedHashMap<>();
                        row.put("nom", e.getKey());
                        row.put("nb", String.valueOf(group.size()));
                        row.put("total", String.format("%,.3f TND", total));
                        row.put("paye", String.format("%,.3f TND", paye));
                        row.put("solde", String.format("%,.3f TND", solde));
                        row.put("etat", solde.compareTo(BigDecimal.ZERO) == 0 ? "Soldé" : "En cours");
                        return row;
                    }).collect(Collectors.toList());

            clientsRapportTable.getItems().setAll(rows);
        } catch (ComptaException e) {
            statusLabel.setText("Erreur clients: " + e.getMessage());
        }
    }

    private void genererRapportFournisseurs(LocalDate debut, LocalDate fin) {
        try {
            List<Invoice> payables = factureService.getAllInvoices().stream()
                    .filter(i -> i.getType() == InvoiceType.PAYABLE
                            && !i.getInvoiceDate().isBefore(debut) && !i.getInvoiceDate().isAfter(fin))
                    .collect(Collectors.toList());

            Map<String, List<Invoice>> byFournisseur = payables.stream()
                    .collect(Collectors.groupingBy(i -> i.getVendorName() != null ? i.getVendorName() : "Inconnu"));

            List<Map<String, String>> rows = byFournisseur.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .map(e -> {
                        List<Invoice> group = e.getValue();
                        BigDecimal total = group.stream().map(Invoice::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                        BigDecimal paye = group.stream().map(Invoice::getPaidAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                        BigDecimal solde = total.subtract(paye);
                        Map<String, String> row = new LinkedHashMap<>();
                        row.put("nom", e.getKey());
                        row.put("nb", String.valueOf(group.size()));
                        row.put("total", String.format("%,.3f TND", total));
                        row.put("paye", String.format("%,.3f TND", paye));
                        row.put("solde", String.format("%,.3f TND", solde));
                        row.put("etat", solde.compareTo(BigDecimal.ZERO) == 0 ? "Soldé" : "En cours");
                        return row;
                    }).collect(Collectors.toList());

            fournisseursRapportTable.getItems().setAll(rows);
        } catch (ComptaException e) {
            statusLabel.setText("Erreur fournisseurs: " + e.getMessage());
        }
    }
}
