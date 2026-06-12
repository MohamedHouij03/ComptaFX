package com.comptafx.presentation;

import com.comptafx.metier.ComptaException;
import com.comptafx.metier.ServiceEcrituresImpl;
import com.comptafx.metier.ServiceFacturesImpl;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Contrôleur pour le Tableau de Bord
 */
public class ControleurTableauBord implements Initializable {
    
    @FXML private Label totalInvoicesLabel;
    @FXML private Label totalEntriesLabel;
    
    // Services
    private final ServiceFacturesImpl invoiceService = new ServiceFacturesImpl();
    private final ServiceEcrituresImpl journalService = new ServiceEcrituresImpl();
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadDashboardData();
    }
    
    @FXML
    private void loadDashboardData() {
        System.out.println("ControleurTableauBord.loadDashboardData() called");
        try {
            // Load dashboard data directly from services
            int totalInvoices = invoiceService.getAllInvoices().size();
            int totalEntries = journalService.getAllEntries().size();
            
            System.out.println("Total invoices: " + totalInvoices);
            System.out.println("Total entries: " + totalEntries);
            
            // Update labels
            if (totalInvoicesLabel != null) {
                totalInvoicesLabel.setText(String.valueOf(totalInvoices));
            } else {
                System.err.println("totalInvoicesLabel is null!");
            }
            
            if (totalEntriesLabel != null) {
                totalEntriesLabel.setText(String.valueOf(totalEntries));
            } else {
                System.err.println("totalEntriesLabel is null!");
            }
            
            System.out.println("Dashboard data loaded successfully");
        } catch (ComptaException e) {
            System.err.println("Error loading dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

