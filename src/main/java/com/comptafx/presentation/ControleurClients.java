package com.comptafx.presentation;

import com.comptafx.entities.Client;
import com.comptafx.entities.TypeClient;
import com.comptafx.metier.ComptaException;
import com.comptafx.metier.ServiceClientsImpl;
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

public class ControleurClients implements Initializable {

    @FXML private TableView<Client> clientsTable;
    @FXML private TableColumn<Client, String> codeColumn;
    @FXML private TableColumn<Client, String> nomColumn;
    @FXML private TableColumn<Client, String> typeColumn;
    @FXML private TableColumn<Client, String> emailColumn;
    @FXML private TableColumn<Client, String> telephoneColumn;
    @FXML private TableColumn<Client, String> villeColumn;
    @FXML private TableColumn<Client, String> soldColumn;
    @FXML private TableColumn<Client, String> actifColumn;

    @FXML private TextField searchField;
    @FXML private ComboBox<String> filtreTypeCombo;
    @FXML private Label statusLabel;
    @FXML private Label totalClientsLabel;
    @FXML private Label clientsActifsLabel;
    @FXML private Label entreprisesLabel;
    @FXML private Label particuliersLabel;

    private final ServiceClientsImpl service = new ServiceClientsImpl();
    private ObservableList<Client> clientsList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupColumns();
        setupFiltres();
        charger();
    }

    private void setupColumns() {
        codeColumn.setCellValueFactory(new PropertyValueFactory<>("code"));
        nomColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        telephoneColumn.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        villeColumn.setCellValueFactory(new PropertyValueFactory<>("ville"));

        typeColumn.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getType().getDisplayName()));

        soldColumn.setCellValueFactory(c ->
                new SimpleStringProperty(String.format("%,.3f TND", c.getValue().getSolde())));

        actifColumn.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().isActif() ? "Actif" : "Inactif"));

        actifColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    setStyle(item.equals("Actif")
                            ? "-fx-text-fill: #2e7d32; -fx-font-weight: bold;"
                            : "-fx-text-fill: #c62828;");
                }
            }
        });
    }

    private void setupFiltres() {
        filtreTypeCombo.setItems(FXCollections.observableArrayList(
                "Tous les types", "Entreprise", "Particulier"));
        filtreTypeCombo.setValue("Tous les types");
        filtreTypeCombo.valueProperty().addListener((obs, old, val) -> filtrer());
        searchField.textProperty().addListener((obs, old, val) -> filtrer());
    }

    private void charger() {
        try {
            List<Client> clients = service.getTousLesClients();
            clientsList.setAll(clients);
            mettreAJourStats(clients);
            filtrer();
            statusLabel.setText(clients.size() + " client(s) chargé(s)");
        } catch (ComptaException e) {
            statusLabel.setText("Erreur: " + e.getMessage());
        }
    }

    private void mettreAJourStats(List<Client> clients) {
        totalClientsLabel.setText(String.valueOf(clients.size()));
        long actifs = clients.stream().filter(Client::isActif).count();
        clientsActifsLabel.setText(String.valueOf(actifs));
        long entreprises = clients.stream()
                .filter(c -> c.getType() == TypeClient.ENTREPRISE).count();
        entreprisesLabel.setText(String.valueOf(entreprises));
        particuliersLabel.setText(String.valueOf(clients.size() - entreprises));
    }

    private void filtrer() {
        String search = searchField.getText().toLowerCase();
        String type = filtreTypeCombo.getValue();

        List<Client> filtered = clientsList.stream()
                .filter(c -> search.isBlank()
                        || (c.getNom() != null && c.getNom().toLowerCase().contains(search))
                        || (c.getCode() != null && c.getCode().toLowerCase().contains(search))
                        || (c.getEmail() != null && c.getEmail().toLowerCase().contains(search))
                        || (c.getTelephone() != null && c.getTelephone().contains(search))
                        || (c.getVille() != null && c.getVille().toLowerCase().contains(search)))
                .filter(c -> type == null || type.equals("Tous les types")
                        || c.getType().getDisplayName().equals(type))
                .collect(Collectors.toList());

        clientsTable.getItems().setAll(filtered);
    }

    @FXML
    private void handleNouveauClient() {
        Dialog<Client> dialog = creerDialog(null);
        dialog.showAndWait().ifPresent(client -> {
            try {
                service.creerClient(client);
                charger();
                statusLabel.setText("Client créé: " + client.getNom());
            } catch (ComptaException e) {
                afficherErreur("Erreur de création", e.getMessage());
            }
        });
    }

    @FXML
    private void handleModifierClient() {
        Client selected = clientsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            afficherErreur("Sélection requise", "Veuillez sélectionner un client.");
            return;
        }
        Dialog<Client> dialog = creerDialog(selected);
        dialog.showAndWait().ifPresent(client -> {
            try {
                service.modifierClient(client);
                charger();
                statusLabel.setText("Client modifié: " + client.getNom());
            } catch (ComptaException e) {
                afficherErreur("Erreur de modification", e.getMessage());
            }
        });
    }

    @FXML
    private void handleSupprimerClient() {
        Client selected = clientsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            afficherErreur("Sélection requise", "Veuillez sélectionner un client.");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmer la suppression");
        confirm.setHeaderText("Supprimer: " + selected.getNom());
        confirm.setContentText("Êtes-vous sûr ? Cette action est irréversible.");
        confirm.showAndWait().ifPresent(r -> {
            if (r == ButtonType.OK) {
                try {
                    service.supprimerClient(selected.getId());
                    charger();
                    statusLabel.setText("Client supprimé: " + selected.getNom());
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

    private Dialog<Client> creerDialog(Client existing) {
        Dialog<Client> dialog = new Dialog<>();
        dialog.setTitle(existing == null ? "Nouveau Client" : "Modifier le Client");

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
        emailField.setPromptText("email@exemple.com");
        TextField telField = new TextField();
        telField.setPromptText("+216 XX XXX XXX");
        TextField villeField = new TextField();
        TextField adresseField = new TextField();
        TextField mfField = new TextField();
        mfField.setPromptText("Matricule fiscal");
        TextField ribField = new TextField();
        ribField.setPromptText("RIB bancaire");
        TextField limiteCreditField = new TextField("0");
        TextArea notesArea = new TextArea();
        notesArea.setPrefRowCount(2);
        CheckBox actifCheck = new CheckBox("Client actif");
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
            limiteCreditField.setText(existing.getLimiteCredit().toPlainString());
            notesArea.setText(existing.getNotes() != null ? existing.getNotes() : "");
            actifCheck.setSelected(existing.isActif());
        } else {
            typeCombo.setValue(TypeClient.ENTREPRISE);
        }

        grid.add(new Label("Nom / Raison Sociale *:"), 0, 0); grid.add(nomField, 1, 0);
        grid.add(new Label("Type *:"), 0, 1); grid.add(typeCombo, 1, 1);
        grid.add(new Label("Email:"), 0, 2); grid.add(emailField, 1, 2);
        grid.add(new Label("Téléphone:"), 0, 3); grid.add(telField, 1, 3);
        grid.add(new Label("Adresse:"), 0, 4); grid.add(adresseField, 1, 4);
        grid.add(new Label("Ville:"), 0, 5); grid.add(villeField, 1, 5);
        grid.add(new Label("Matricule Fiscal:"), 0, 6); grid.add(mfField, 1, 6);
        grid.add(new Label("RIB:"), 0, 7); grid.add(ribField, 1, 7);
        grid.add(new Label("Limite Crédit (TND):"), 0, 8); grid.add(limiteCreditField, 1, 8);
        grid.add(new Label("Notes:"), 0, 9); grid.add(notesArea, 1, 9);
        grid.add(actifCheck, 1, 10);

        content.getChildren().add(grid);
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().setPrefSize(500, 550);

        dialog.setResultConverter(btn -> {
            if (btn == saveBtn) {
                Client c = existing != null ? existing : new Client();
                c.setNom(nomField.getText().trim());
                c.setType(typeCombo.getValue());
                c.setEmail(emailField.getText().trim());
                c.setTelephone(telField.getText().trim());
                c.setAdresse(adresseField.getText().trim());
                c.setVille(villeField.getText().trim());
                c.setMatriculeFiscal(mfField.getText().trim());
                c.setRib(ribField.getText().trim());
                c.setNotes(notesArea.getText().trim());
                c.setActif(actifCheck.isSelected());
                try {
                    c.setLimiteCredit(new BigDecimal(limiteCreditField.getText().trim()));
                } catch (NumberFormatException ex) {
                    c.setLimiteCredit(BigDecimal.ZERO);
                }
                return c;
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
