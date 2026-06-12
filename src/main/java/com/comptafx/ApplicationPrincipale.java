package com.comptafx;

import com.comptafx.dao.DatabaseConfig;
import com.comptafx.dao.DatabaseException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main JavaFX Application class for ComptaFX Accounting System
 */
public class ApplicationPrincipale extends Application {
    
    private static Stage primaryStage;
    
    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        
        // Initialize database
        try {
            DatabaseConfig.initializeDatabase();
            // Drop unnecessary tables (bank_accounts and bank_transactions)
            DatabaseConfig.dropUnnecessaryTables();
        } catch (DatabaseException e) {
            System.err.println("Database initialization failed: " + e.getMessage());
        }
        
        // Charger la vue principale
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/VuePrincipale.fxml"));
        Parent root = loader.load();
        
        Scene scene = new Scene(root, 1200, 700);
        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
        
        stage.setTitle("ComptaFX - Système de Gestion Comptable");
        stage.setScene(scene);
        stage.setMinWidth(1000);
        stage.setMinHeight(600);
        stage.show();
    }
    
    @Override
    public void stop() {
        // Clean up database connection on exit
        DatabaseConfig.closeConnection();
    }
    
    public static Stage getPrimaryStage() {
        return primaryStage;
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}

