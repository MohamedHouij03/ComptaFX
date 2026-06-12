package com.comptafx.presentation;

import com.comptafx.entities.Fournisseur;
import com.comptafx.entities.TypeClient;
import com.comptafx.metier.ComptaException;
import com.comptafx.metier.ServiceFournisseursImpl;
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
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ControleurFournisseurs implements Initializable {

    @FXML private TableView<Fournisseur> fournisseursTable;
    @FXML private TableColumn<Fournisseur, String> codeColumn;
    @FXML private TableColumn<Fournisseur, String> nomColumn;
    @FXML private TableColumn<Fournisseur, String> typeColumn;
    @FXML private TableColumn<Fournisseur, String> categorieColumn;
    @FXML private TableColumn<Fournisseur, String> emailColumn;
    @FXML private TableColumn<Fournisseur, String> telephoneColumn;
    @FXML private TableColumn<Fournisseur, String> delaiColumn;
    @FXML private TableColumn<Fournisseur, String> soldeColumn;
    @FXML private TableColumn<Fournisseur, String> actifColumn;

    @FXML private TextField searchField;
    @FXML private ComboBox<String> filtreCategorieCombo;
    @FXML private Label statusLabel;
    @FXML private Label totalFournisseursLabel;
    @FXML private Label fournisseursActifsLabel;
    @FXML private Label delaiMoyenLabel;
    @FXML private Label encoursTotalLabel;

    private final ServiceFournisseursImpl service = new ServiceFournisseursImpl();
    private ObservableList<Fournisseur> fournisseursList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupColumns();
        setupFiltres();
        charger();
    }

    private void setupColumns() {
        codeColumn.setCellValueFactory(new PropertyValueFactory<>("code"));
        nomColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        categorieColumn.setCellValueFactory(new PropertyValueFactory<>("categorie"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        telephoneColumn.setCellValueFactory(new PropertyValueFactory<>("telephone"));

        typeColumn.setCellValueFactory(f ->
                new SimpleStringProperty(f.getValue().getType().getDisplayName()));

        delaiColumn.setCellValueFactory(f ->
                new SimpleStringProperty(f.getValue().getDelaiPaiement() + " j"));

        soldeColumn.setCellValueFactory(f ->
                new SimpleStringProperty(String.format("%,.3f TND", f.getValue().getSolde())));

        actifColumn.setCellValueFactory(f ->
                new SimpleStringProperty(f.getValue().isActif() ? "Actif" : "Inactif"));

        actifColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); }
                else {
                    setText(item);
                    setStyle(item.equals("Actif")
                            ? "-fx-text-fill: #2e7d32; -fx-font-weight: bold;"
                            : "-fx-text-fill: #c62828;");
                }
            }
        });
    }

    private void setupFiltres() {
        filtreCategorieCombo.setItems(FXCollections.observableArrayList(
                "Toutes catégories", "Services", "Matières premières", "Équipements", "Informatique", "Autre"));
        filtreCategorieCombo.setValue("Toutes catégories");
        filtreCategorieCombo.valueProperty().addListener((obs, old, val) -> filtrer());
        searchField.textProperty().addListener((obs, old, val) -> filtrer());
    }

    private void charger() {
        try {
            List<Fournisseur> list = service.getTousLesFournisseurs();
            fournisseursList.setAll(list);
            mettreAJourStats(list);
            filtrer();
            statusLabel.setText(list.size() + " fournisseur(s) chargé(s)");
        } catch (ComptaException e) {
            statusLabel.setText("Erreur: " + e.getMessage());
        }
    }

    private void mettreAJourStats(List<Fournisseur> list) {
        totalFournisseursLabel.setText(String.valueOf(list.size()));
        long actifs = list.stream().filter(Fournisseur::isActif).count();
        fournisseursActifsLabel.setText(String.valueOf(actifs));

        double delaiMoyen = list.stream()
                .mapToInt(Fournisseur::getDelaiPaiement).average().orElse(0);
        delaiMoyenLabel.setText(String.format("%.0f jours", delaiMoyen));

        BigDecimal encours = list.stream()
                .map(Fournisseur::getSolde).reduce(BigDecimal.ZERO, BigDecimal::add);
        encoursTotalLabel.setText(String.format("%,.3f TND", encours));
    }

    private void filtrer() {
        String search = searchField.getText().toLowerCase();
        String categorie = filtreCategorieCombo.getValue();

        List<Fournisseur> filtered = fournisseursList.stream()
                .filter(f -> search.isBlank()
                        || (f.getNom() != null && f.getNom().toLowerCase().contains(search))
                        || (f.getCode() != null && f.getCode().toLowerCase().contains(search))
                        || (f.getEmail() != null && f.getEmail().toLowerCase().contains(search)))
                .filter(f -> categorie == null || categorie.equals("Toutes catégories")
                        || categorie.equals(f.getCategorie()))
                .collect(Collectors.toList());

        fournisseursTable.getItems().setAll(filtered);
    }

    @FXML
    private void handleNouveauFournisseur() {
        Dialog<Fournisseur> dialog = creerDialog(null);
        dialog.showAndWait().ifPresent(f -> {
            try {
                service.creerFournisseur(f);
                charger();
                statusLabel.setText("Fournisseur créé: " + f.getNom());
            } catch (ComptaException e) {
                afficherErreur("Erreur de création", e.getMessage());
            }
        });
    }

    @FXML
    private void handleModifierFournisseur() {
        Fournisseur selected = fournisseursTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            afficherErreur("Sélection requise", "Veuillez sélectionner un fournisseur.");
            return;
        }
        Dialog<Fournisseur> dialog = creerDialog(selected);
        dialog.showAndWait().ifPresent(f -> {
            try {
                service.modifierFournisseur(f);
                charger();
                statusLabel.setText("Fournisseur modifié: " + f.getNom());
            } catch (ComptaException e) {
                afficherErreur("Erreur de modification", e.getMessage());
            }
        });
    }

    @FXML
    private void handleSupprimerFournisseur() {
        Fournisseur selected = fournisseursTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            afficherErreur("Sélection requise", "Veuillez sélectionner un fournisseur.");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmer la suppression");
        confirm.setHeaderText("Supprimer: " + selected.getNom());
        confirm.setContentText("Êtes-vous sûr ? Cette action est irréversible.");
        confirm.showAndWait().ifPresent(r -> {
            if (r == ButtonType.OK) {
                try {
                    service.supprimerFournisseur(selected.getId());
                    charger();
                    statusLabel.setText("Fournisseur supprimé.");
                } catch (ComptaException e) {
                    afficherErreur("Erreur de suppression", e.getMessage());
                }
            }
        });
    }

    @FXML
    private void handleActualiser() {
        charger();
    }

    private Dialog<Fournisseur> creerDialog(Fournisseur existing) {
        Dialog<Fournisseur> dialog = new Dialog<>();
        dialog.setTitle(existing == null ? "Nouveau Fournisseur" : "Modifier le Fournisseur");

        ButtonType saveBtn = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        VBox content = new VBox(12);
        content.setPadding(new Insets(20));

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(10);

        TextField nomField = new TextField();
        nomField.setPromptText("Raison sociale ou nom complet");
        nomField.setPrefWidth(250);

        ComboBox<TypeClient> typeCombo = new ComboBox<>(
                FXCollections.observableArrayList(TypeClient.values()));
        typeCombo.setConverter(new javafx.util.StringConverter<>() {
            @Override public String toString(TypeClient t) { return t == null ? "" : t.getDisplayName(); }
            @Override public TypeClient fromString(String s) { return null; }
        });

        TextField emailField = new TextField();
        TextField telField = new TextField();
        TextField villeField = new TextField();
        TextField adresseField = new TextField();
        TextField mfField = new TextField();
        TextField ribField = new TextField();
        TextField delaiField = new TextField("30");
        ComboBox<String> categorieCombo = new ComboBox<>(FXCollections.observableArrayList(
                "Services", "Matières premières", "Équipements", "Informatique", "Autre"));
        TextArea notesArea = new TextArea();
        notesArea.setPrefRowCount(2);
        CheckBox actifCheck = new CheckBox("Fournisseur actif");
        actifCheck.setSelected(true);

        if (existing != null) {
            nomField.setText(existing.getNom());
            typeCombo.setValue(existing.getType());
            emailField.setText(existing.getEmail() != null ? existing.getEmail() : "");
            telField.setText(existing.getTelephone() != null ? existing.getTelephone() : "");
            villeField.setText(existing.getVille() != null ? existing.getVille() : "");
            adresseField.setText(existing.getAdresse() != null ? existing.getAdresse() : "");
            mfField.setText(existing.getMatriculeFiscal() != null ? existing.getMatriculeFiscal() : "");
            ribField.setText(existing.getRib() != null ? existing.getRib() : "");
            delaiField.setText(String.valueOf(existing.getDelaiPaiement()));
            categorieCombo.setValue(existing.getCategorie());
            notesArea.setText(existing.getNotes() != null ? existing.getNotes() : "");
            actifCheck.setSelected(existing.isActif());
        } else {
            typeCombo.setValue(TypeClient.ENTREPRISE);
        }

        grid.add(new Label("Nom / Raison Sociale *:"), 0, 0); grid.add(nomField, 1, 0);
        grid.add(new Label("Type *:"), 0, 1); grid.add(typeCombo, 1, 1);
        grid.add(new Label("Catégorie:"), 0, 2); grid.add(categorieCombo, 1, 2);
        grid.add(new Label("Email:"), 0, 3); grid.add(emailField, 1, 3);
        grid.add(new Label("Téléphone:"), 0, 4); grid.add(telField, 1, 4);
        grid.add(new Label("Adresse:"), 0, 5); grid.add(adresseField, 1, 5);
        grid.add(new Label("Ville:"), 0, 6); grid.add(villeField, 1, 6);
        grid.add(new Label("Matricule Fiscal:"), 0, 7); grid.add(mfField, 1, 7);
        grid.add(new Label("RIB:"), 0, 8); grid.add(ribField, 1, 8);
        grid.add(new Label("Délai paiement (j):"), 0, 9); grid.add(delaiField, 1, 9);
        grid.add(new Label("Notes:"), 0, 10); grid.add(notesArea, 1, 10);
        grid.add(actifCheck, 1, 11);

        content.getChildren().add(grid);
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().setPrefSize(500, 580);

        dialog.setResultConverter(btn -> {
            if (btn == saveBtn) {
                Fournisseur f = existing != null ? existing : new Fournisseur();
                f.setNom(nomField.getText().trim());
                f.setType(typeCombo.getValue());
                f.setCategorie(categorieCombo.getValue());
                f.setEmail(emailField.getText().trim());
                f.setTelephone(telField.getText().trim());
                f.setAdresse(adresseField.getText().trim());
                f.setVille(villeField.getText().trim());
                f.setMatriculeFiscal(mfField.getText().trim());
                f.setRib(ribField.getText().trim());
                f.setNotes(notesArea.getText().trim());
                f.setActif(actifCheck.isSelected());
                try { f.setDelaiPaiement(Integer.parseInt(delaiField.getText().trim())); }
                catch (NumberFormatException ex) { f.setDelaiPaiement(30); }
                return f;
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
