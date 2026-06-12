package com.comptafx.presentation;

import com.comptafx.dao.DatabaseConfig;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

public class ControleurParametres implements Initializable {

    // Société
    @FXML private TextField raisonSocialeField;
    @FXML private TextField matriculeFiscalField;
    @FXML private TextField adresseField;
    @FXML private TextField villeField;
    @FXML private TextField telephoneField;
    @FXML private TextField emailField;
    @FXML private TextField siteWebField;

    // Comptable
    @FXML private ComboBox<String> deviseCombo;
    @FXML private TextField tauxTvaField;
    @FXML private TextField prefixeFactureField;
    @FXML private TextField delaiPaiementField;
    @FXML private ComboBox<String> exerciceFiscalCombo;

    // BDD
    @FXML private TextField dbHostField;
    @FXML private TextField dbPortField;
    @FXML private TextField dbNameField;
    @FXML private Label dbStatusLabel;

    // Apparence
    @FXML private ComboBox<String> langueCombo;
    @FXML private CheckBox afficherIACheckBox;
    @FXML private CheckBox notificationsCheckBox;

    @FXML private Label statusLabel;

    private static final String PREF_NODE = "com/comptafx/parametres";
    private Preferences prefs;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        prefs = Preferences.userRoot().node(PREF_NODE);
        setupComboBoxes();
        chargerParametres();
        verifierConnexion();
    }

    private void setupComboBoxes() {
        deviseCombo.setItems(FXCollections.observableArrayList("TND", "EUR", "USD", "GBP", "MAD"));
        deviseCombo.setValue("TND");

        int currentYear = LocalDate.now().getYear();
        exerciceFiscalCombo.setItems(FXCollections.observableArrayList(
                String.valueOf(currentYear - 1), String.valueOf(currentYear), String.valueOf(currentYear + 1)));
        exerciceFiscalCombo.setValue(String.valueOf(currentYear));

        langueCombo.setItems(FXCollections.observableArrayList("Français", "Arabe", "Anglais"));
        langueCombo.setValue("Français");
    }

    private void chargerParametres() {
        raisonSocialeField.setText(prefs.get("raison_sociale", "Société par Défaut"));
        matriculeFiscalField.setText(prefs.get("matricule_fiscal", ""));
        adresseField.setText(prefs.get("adresse", ""));
        villeField.setText(prefs.get("ville", "Tunis"));
        telephoneField.setText(prefs.get("telephone", ""));
        emailField.setText(prefs.get("email", ""));
        siteWebField.setText(prefs.get("site_web", ""));

        String devise = prefs.get("devise", "TND");
        if (deviseCombo.getItems().contains(devise)) deviseCombo.setValue(devise);
        tauxTvaField.setText(prefs.get("taux_tva", "19"));
        prefixeFactureField.setText(prefs.get("prefixe_facture", "FAC"));
        delaiPaiementField.setText(prefs.get("delai_paiement", "30"));

        String exercice = prefs.get("exercice_fiscal", String.valueOf(LocalDate.now().getYear()));
        if (exerciceFiscalCombo.getItems().contains(exercice)) exerciceFiscalCombo.setValue(exercice);

        dbHostField.setText(prefs.get("db_host", "localhost"));
        dbPortField.setText(prefs.get("db_port", "3306"));
        dbNameField.setText(prefs.get("db_name", "comptafx"));

        String langue = prefs.get("langue", "Français");
        if (langueCombo.getItems().contains(langue)) langueCombo.setValue(langue);
        afficherIACheckBox.setSelected(prefs.getBoolean("afficher_ia", true));
        notificationsCheckBox.setSelected(prefs.getBoolean("notifications", true));
    }

    @FXML
    private void handleEnregistrer() {
        prefs.put("raison_sociale", raisonSocialeField.getText().trim());
        prefs.put("matricule_fiscal", matriculeFiscalField.getText().trim());
        prefs.put("adresse", adresseField.getText().trim());
        prefs.put("ville", villeField.getText().trim());
        prefs.put("telephone", telephoneField.getText().trim());
        prefs.put("email", emailField.getText().trim());
        prefs.put("site_web", siteWebField.getText().trim());

        prefs.put("devise", deviseCombo.getValue() != null ? deviseCombo.getValue() : "TND");
        prefs.put("taux_tva", tauxTvaField.getText().trim());
        prefs.put("prefixe_facture", prefixeFactureField.getText().trim());
        prefs.put("delai_paiement", delaiPaiementField.getText().trim());
        prefs.put("exercice_fiscal", exerciceFiscalCombo.getValue() != null ? exerciceFiscalCombo.getValue() : "");

        prefs.put("db_host", dbHostField.getText().trim());
        prefs.put("db_port", dbPortField.getText().trim());
        prefs.put("db_name", dbNameField.getText().trim());

        prefs.put("langue", langueCombo.getValue() != null ? langueCombo.getValue() : "Français");
        prefs.putBoolean("afficher_ia", afficherIACheckBox.isSelected());
        prefs.putBoolean("notifications", notificationsCheckBox.isSelected());

        statusLabel.setText("Paramètres enregistrés avec succès.");
        afficherInfo("Paramètres sauvegardés", "Vos paramètres ont été enregistrés.\nCertains changements nécessitent un redémarrage de l'application.");
    }

    @FXML
    private void handleReinitialiser() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Réinitialiser");
        confirm.setContentText("Réinitialiser tous les paramètres aux valeurs par défaut ?");
        confirm.showAndWait().ifPresent(r -> {
            if (r == ButtonType.OK) {
                try { prefs.clear(); } catch (java.util.prefs.BackingStoreException e) {
                    statusLabel.setText("Erreur: " + e.getMessage());
                    return;
                }
                chargerParametres();
                statusLabel.setText("Paramètres réinitialisés.");
            }
        });
    }

    @FXML
    private void handleTesterConnexion() {
        verifierConnexion();
    }

    private void verifierConnexion() {
        boolean ok = DatabaseConfig.testConnection();
        if (ok) {
            dbStatusLabel.setText("Connecté");
            dbStatusLabel.setStyle("-fx-text-fill: #2e7d32; -fx-font-weight: bold;");
            statusLabel.setText("Connexion à la base de données: OK");
        } else {
            dbStatusLabel.setText("Déconnecté");
            dbStatusLabel.setStyle("-fx-text-fill: #c62828; -fx-font-weight: bold;");
            statusLabel.setText("Connexion à la base de données: ÉCHEC — Vérifiez MySQL");
        }
    }

    private void afficherInfo(String titre, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public static String getRaisonSociale() {
        Preferences p = Preferences.userRoot().node(PREF_NODE);
        return p.get("raison_sociale", "Société par Défaut");
    }
}
