package com.comptafx.presentation;

import com.comptafx.entities.CompteBancaire;
import com.comptafx.entities.TransactionBancaire;
import com.comptafx.entities.TypeTransaction;
import com.comptafx.metier.ComptaException;
import com.comptafx.metier.ServiceTresorerieImpl;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ControleurTresorerie implements Initializable {

    // Comptes
    @FXML private TableView<CompteBancaire> comptesTable;
    @FXML private TableColumn<CompteBancaire, String> banqueColumn;
    @FXML private TableColumn<CompteBancaire, String> intituleColumn;
    @FXML private TableColumn<CompteBancaire, String> soldeColumn;

    // Transactions
    @FXML private TableView<TransactionBancaire> transactionsTable;
    @FXML private TableColumn<TransactionBancaire, String> dateTransCol;
    @FXML private TableColumn<TransactionBancaire, String> libelleTransCol;
    @FXML private TableColumn<TransactionBancaire, String> typeTransCol;
    @FXML private TableColumn<TransactionBancaire, String> referenceTransCol;
    @FXML private TableColumn<TransactionBancaire, String> debitTransCol;
    @FXML private TableColumn<TransactionBancaire, String> creditTransCol;

    @FXML private Label compteSelectionneLabel;
    @FXML private TextField searchTransactionField;
    @FXML private ComboBox<String> filtreTypeTransactionCombo;
    @FXML private Label statusLabel;
    @FXML private Label totalTresorerieLabel;
    @FXML private Label comptesActifsLabel;
    @FXML private Label entreesLabel;
    @FXML private Label sortiesLabel;

    private final ServiceTresorerieImpl service = new ServiceTresorerieImpl();
    private ObservableList<CompteBancaire> comptesList = FXCollections.observableArrayList();
    private ObservableList<TransactionBancaire> transactionsList = FXCollections.observableArrayList();
    private CompteBancaire compteSelectionne;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupComptesColumns();
        setupTransactionsColumns();
        setupFiltres();
        chargerComptes();

        comptesTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, compte) -> {
                    if (compte != null) selectionnerCompte(compte);
                });
    }

    private void setupComptesColumns() {
        banqueColumn.setCellValueFactory(new PropertyValueFactory<>("banque"));
        intituleColumn.setCellValueFactory(new PropertyValueFactory<>("intitule"));
        soldeColumn.setCellValueFactory(c ->
                new SimpleStringProperty(String.format("%,.3f %s",
                        c.getValue().getSoldeActuel(), c.getValue().getDevise())));

        soldeColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); }
                else {
                    setText(item);
                    // Color based on positive/negative
                    CompteBancaire cb = getTableRow().getItem();
                    if (cb != null) {
                        setStyle(cb.getSoldeActuel().compareTo(BigDecimal.ZERO) >= 0
                                ? "-fx-text-fill: #2e7d32; -fx-font-weight: bold;"
                                : "-fx-text-fill: #c62828; -fx-font-weight: bold;");
                    }
                }
            }
        });
    }

    private void setupTransactionsColumns() {
        dateTransCol.setCellValueFactory(t ->
                new SimpleStringProperty(t.getValue().getDateTransaction().toString()));
        libelleTransCol.setCellValueFactory(new PropertyValueFactory<>("libelle"));
        typeTransCol.setCellValueFactory(t ->
                new SimpleStringProperty(t.getValue().getType().getDisplayName()));
        referenceTransCol.setCellValueFactory(new PropertyValueFactory<>("reference"));
        debitTransCol.setCellValueFactory(t -> {
            BigDecimal d = t.getValue().getDebit();
            return new SimpleStringProperty(d.compareTo(BigDecimal.ZERO) > 0
                    ? String.format("%,.3f", d) : "");
        });
        creditTransCol.setCellValueFactory(t -> {
            BigDecimal c = t.getValue().getCredit();
            return new SimpleStringProperty(c.compareTo(BigDecimal.ZERO) > 0
                    ? String.format("%,.3f", c) : "");
        });

        // Color rows: debit=red, credit=green
        transactionsTable.setRowFactory(tv -> {
            TableRow<TransactionBancaire> row = new TableRow<>();
            row.itemProperty().addListener((obs, old, t) -> {
                if (t != null) {
                    if (t.getDebit().compareTo(BigDecimal.ZERO) > 0)
                        row.setStyle("-fx-background-color: #fff5f5;");
                    else if (t.getCredit().compareTo(BigDecimal.ZERO) > 0)
                        row.setStyle("-fx-background-color: #f1f8f1;");
                    else row.setStyle("");
                } else row.setStyle("");
            });
            return row;
        });
    }

    private void setupFiltres() {
        List<String> types = java.util.stream.Stream.concat(
                java.util.stream.Stream.of("Tous types"),
                java.util.Arrays.stream(TypeTransaction.values()).map(TypeTransaction::getDisplayName)
        ).collect(Collectors.toList());
        filtreTypeTransactionCombo.setItems(FXCollections.observableArrayList(types));
        filtreTypeTransactionCombo.setValue("Tous types");
        filtreTypeTransactionCombo.valueProperty().addListener((obs, old, val) -> filtrerTransactions());
        searchTransactionField.textProperty().addListener((obs, old, val) -> filtrerTransactions());
    }

    private void chargerComptes() {
        try {
            List<CompteBancaire> comptes = service.getTousLesComptes();
            comptesList.setAll(comptes);
            comptesTable.getItems().setAll(comptes);
            mettreAJourKPI(comptes);
            statusLabel.setText(comptes.size() + " compte(s) chargé(s)");
        } catch (ComptaException e) {
            statusLabel.setText("Erreur: " + e.getMessage());
        }
    }

    private void mettreAJourKPI(List<CompteBancaire> comptes) {
        BigDecimal total = comptes.stream()
                .filter(CompteBancaire::isActif)
                .map(CompteBancaire::getSoldeActuel)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        totalTresorerieLabel.setText(String.format("%,.3f TND", total));
        comptesActifsLabel.setText(String.valueOf(comptes.stream().filter(CompteBancaire::isActif).count()));

        // Entrées/Sorties du mois courant depuis les transactions
        LocalDate debutMois = LocalDate.now().withDayOfMonth(1);
        BigDecimal entrees = BigDecimal.ZERO;
        BigDecimal sorties = BigDecimal.ZERO;
        for (CompteBancaire c : comptes) {
            try {
                List<TransactionBancaire> txs = service.getTransactions(c.getId());
                for (TransactionBancaire t : txs) {
                    if (!t.getDateTransaction().isBefore(debutMois)) {
                        entrees = entrees.add(t.getCredit());
                        sorties = sorties.add(t.getDebit());
                    }
                }
            } catch (ComptaException ignored) {}
        }
        entreesLabel.setText(String.format("%,.3f TND", entrees));
        sortiesLabel.setText(String.format("%,.3f TND", sorties));
    }

    private void selectionnerCompte(CompteBancaire compte) {
        compteSelectionne = compte;
        compteSelectionneLabel.setText(compte.getIntitule() + " — " + compte.getBanque());
        try {
            List<TransactionBancaire> txs = service.getTransactions(compte.getId());
            transactionsList.setAll(txs);
            filtrerTransactions();
        } catch (ComptaException e) {
            statusLabel.setText("Erreur: " + e.getMessage());
        }
    }

    private void filtrerTransactions() {
        String search = searchTransactionField.getText().toLowerCase();
        String type = filtreTypeTransactionCombo.getValue();

        List<TransactionBancaire> filtered = transactionsList.stream()
                .filter(t -> search.isBlank()
                        || (t.getLibelle() != null && t.getLibelle().toLowerCase().contains(search))
                        || (t.getReference() != null && t.getReference().toLowerCase().contains(search)))
                .filter(t -> type == null || type.equals("Tous types")
                        || t.getType().getDisplayName().equals(type))
                .collect(Collectors.toList());

        transactionsTable.getItems().setAll(filtered);
    }

    @FXML
    private void handleAjouterCompte() {
        Dialog<CompteBancaire> dialog = creerDialogCompte(null);
        dialog.showAndWait().ifPresent(c -> {
            try {
                service.creerCompte(c);
                chargerComptes();
                statusLabel.setText("Compte créé: " + c.getIntitule());
            } catch (ComptaException e) {
                afficherErreur("Erreur", e.getMessage());
            }
        });
    }

    @FXML
    private void handleModifierCompte() {
        CompteBancaire selected = comptesTable.getSelectionModel().getSelectedItem();
        if (selected == null) { afficherErreur("Sélection requise", "Sélectionnez un compte."); return; }
        Dialog<CompteBancaire> dialog = creerDialogCompte(selected);
        dialog.showAndWait().ifPresent(c -> {
            try {
                service.modifierCompte(c);
                chargerComptes();
                statusLabel.setText("Compte modifié: " + c.getIntitule());
            } catch (ComptaException e) {
                afficherErreur("Erreur", e.getMessage());
            }
        });
    }

    @FXML
    private void handleSupprimerCompte() {
        CompteBancaire selected = comptesTable.getSelectionModel().getSelectedItem();
        if (selected == null) { afficherErreur("Sélection requise", "Sélectionnez un compte."); return; }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmer");
        confirm.setContentText("Supprimer le compte \"" + selected.getIntitule() + "\" et toutes ses transactions ?");
        confirm.showAndWait().ifPresent(r -> {
            if (r == ButtonType.OK) {
                try {
                    service.supprimerCompte(selected.getId());
                    compteSelectionne = null;
                    transactionsList.clear();
                    transactionsTable.getItems().clear();
                    compteSelectionneLabel.setText("Sélectionnez un compte");
                    chargerComptes();
                } catch (ComptaException e) {
                    afficherErreur("Erreur", e.getMessage());
                }
            }
        });
    }

    @FXML
    private void handleNouvelleTransaction() {
        if (compteSelectionne == null) {
            afficherErreur("Compte requis", "Sélectionnez d'abord un compte bancaire.");
            return;
        }
        Dialog<TransactionBancaire> dialog = creerDialogTransaction();
        dialog.showAndWait().ifPresent(t -> {
            try {
                t.setCompteBancaireId(compteSelectionne.getId());
                service.ajouterTransaction(t);
                selectionnerCompte(compteSelectionne);
                chargerComptes();
                statusLabel.setText("Transaction ajoutée.");
            } catch (ComptaException e) {
                afficherErreur("Erreur", e.getMessage());
            }
        });
    }

    @FXML
    private void handleSupprimerTransaction() {
        TransactionBancaire selected = transactionsTable.getSelectionModel().getSelectedItem();
        if (selected == null) { afficherErreur("Sélection requise", "Sélectionnez une transaction."); return; }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmer");
        confirm.setContentText("Supprimer cette transaction ?");
        confirm.showAndWait().ifPresent(r -> {
            if (r == ButtonType.OK) {
                try {
                    service.supprimerTransaction(selected.getId());
                    if (compteSelectionne != null) selectionnerCompte(compteSelectionne);
                    chargerComptes();
                } catch (ComptaException e) {
                    afficherErreur("Erreur", e.getMessage());
                }
            }
        });
    }

    private Dialog<CompteBancaire> creerDialogCompte(CompteBancaire existing) {
        Dialog<CompteBancaire> dialog = new Dialog<>();
        dialog.setTitle(existing == null ? "Nouveau Compte Bancaire" : "Modifier le Compte");

        ButtonType saveBtn = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(12); grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField banqueField = new TextField();
        banqueField.setPromptText("Nom de la banque");
        banqueField.setPrefWidth(240);
        TextField intituleField = new TextField();
        intituleField.setPromptText("Intitulé du compte");
        TextField numField = new TextField();
        numField.setPromptText("Numéro de compte");
        TextField ribField = new TextField();
        TextField soldeInitField = new TextField("0");
        ComboBox<String> deviseCombo = new ComboBox<>(
                FXCollections.observableArrayList("TND", "EUR", "USD", "GBP"));
        TextArea notesArea = new TextArea();
        notesArea.setPrefRowCount(2);
        CheckBox actifCheck = new CheckBox("Compte actif");
        actifCheck.setSelected(true);

        if (existing != null) {
            banqueField.setText(existing.getBanque() != null ? existing.getBanque() : "");
            intituleField.setText(existing.getIntitule() != null ? existing.getIntitule() : "");
            numField.setText(existing.getNumeroCompte() != null ? existing.getNumeroCompte() : "");
            ribField.setText(existing.getRib() != null ? existing.getRib() : "");
            soldeInitField.setText(existing.getSoldeInitial().toPlainString());
            deviseCombo.setValue(existing.getDevise());
            notesArea.setText(existing.getNotes() != null ? existing.getNotes() : "");
            actifCheck.setSelected(existing.isActif());
        } else {
            deviseCombo.setValue("TND");
        }

        grid.add(new Label("Banque *:"), 0, 0); grid.add(banqueField, 1, 0);
        grid.add(new Label("Intitulé *:"), 0, 1); grid.add(intituleField, 1, 1);
        grid.add(new Label("N° Compte:"), 0, 2); grid.add(numField, 1, 2);
        grid.add(new Label("RIB:"), 0, 3); grid.add(ribField, 1, 3);
        grid.add(new Label("Solde Initial:"), 0, 4); grid.add(soldeInitField, 1, 4);
        grid.add(new Label("Devise:"), 0, 5); grid.add(deviseCombo, 1, 5);
        grid.add(new Label("Notes:"), 0, 6); grid.add(notesArea, 1, 6);
        grid.add(actifCheck, 1, 7);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().setPrefSize(440, 420);

        dialog.setResultConverter(btn -> {
            if (btn == saveBtn) {
                CompteBancaire c = existing != null ? existing : new CompteBancaire();
                c.setBanque(banqueField.getText().trim());
                c.setIntitule(intituleField.getText().trim());
                c.setNumeroCompte(numField.getText().trim());
                c.setRib(ribField.getText().trim());
                c.setDevise(deviseCombo.getValue());
                c.setNotes(notesArea.getText().trim());
                c.setActif(actifCheck.isSelected());
                try { c.setSoldeInitial(new BigDecimal(soldeInitField.getText().trim())); }
                catch (NumberFormatException ex) { c.setSoldeInitial(BigDecimal.ZERO); }
                return c;
            }
            return null;
        });
        return dialog;
    }

    private Dialog<TransactionBancaire> creerDialogTransaction() {
        Dialog<TransactionBancaire> dialog = new Dialog<>();
        dialog.setTitle("Nouvelle Transaction");

        ButtonType saveBtn = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(12); grid.setVgap(10);
        grid.setPadding(new Insets(20));

        DatePicker datePicker = new DatePicker(LocalDate.now());
        TextField libelleField = new TextField();
        libelleField.setPromptText("Description de la transaction");
        libelleField.setPrefWidth(240);
        TextField referenceField = new TextField();

        ComboBox<TypeTransaction> typeCombo = new ComboBox<>(
                FXCollections.observableArrayList(TypeTransaction.values()));
        typeCombo.setConverter(new javafx.util.StringConverter<>() {
            @Override public String toString(TypeTransaction t) { return t == null ? "" : t.getDisplayName(); }
            @Override public TypeTransaction fromString(String s) { return null; }
        });
        typeCombo.setValue(TypeTransaction.VIREMENT);

        ToggleGroup sens = new ToggleGroup();
        RadioButton debitBtn = new RadioButton("Débit (sortie)");
        RadioButton creditBtn = new RadioButton("Crédit (entrée)");
        debitBtn.setToggleGroup(sens);
        creditBtn.setToggleGroup(sens);
        creditBtn.setSelected(true);

        TextField montantField = new TextField("0");
        TextArea notesArea = new TextArea();
        notesArea.setPrefRowCount(2);

        grid.add(new Label("Date:"), 0, 0); grid.add(datePicker, 1, 0);
        grid.add(new Label("Libellé *:"), 0, 1); grid.add(libelleField, 1, 1);
        grid.add(new Label("Type:"), 0, 2); grid.add(typeCombo, 1, 2);
        grid.add(new Label("Référence:"), 0, 3); grid.add(referenceField, 1, 3);
        grid.add(new Label("Sens:"), 0, 4);
        VBox sensBox = new VBox(5, creditBtn, debitBtn);
        grid.add(sensBox, 1, 4);
        grid.add(new Label("Montant (TND):"), 0, 5); grid.add(montantField, 1, 5);
        grid.add(new Label("Notes:"), 0, 6); grid.add(notesArea, 1, 6);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().setPrefSize(420, 400);

        dialog.setResultConverter(btn -> {
            if (btn == saveBtn) {
                TransactionBancaire t = new TransactionBancaire();
                t.setDateTransaction(datePicker.getValue());
                t.setLibelle(libelleField.getText().trim());
                t.setType(typeCombo.getValue());
                t.setReference(referenceField.getText().trim());
                t.setNotes(notesArea.getText().trim());
                BigDecimal montant = BigDecimal.ZERO;
                try { montant = new BigDecimal(montantField.getText().trim()); } catch (NumberFormatException ignored) {}
                if (debitBtn.isSelected()) t.setDebit(montant);
                else t.setCredit(montant);
                return t;
            }
            return null;
        });
        return dialog;
    }

    private void afficherErreur(String titre, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
