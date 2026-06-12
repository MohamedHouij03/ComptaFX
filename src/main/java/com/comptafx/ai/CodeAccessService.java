package com.comptafx.ai;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Service pour accéder au code source de l'application
 */
public class CodeAccessService {
    
    private static final String PROJECT_ROOT = System.getProperty("user.dir");
    
    /**
     * Lit le contenu d'un fichier source
     */
    public String readFile(String relativePath) {
        try {
            Path filePath = Paths.get(PROJECT_ROOT, relativePath);
            return Files.readString(filePath);
        } catch (IOException e) {
            return "Erreur lors de la lecture du fichier: " + e.getMessage();
        }
    }
    
    /**
     * Liste tous les fichiers Java dans le projet
     */
    public List<String> listJavaFiles() {
        List<String> javaFiles = new ArrayList<>();
        try {
            Path srcPath = Paths.get(PROJECT_ROOT, "src", "main", "java");
            if (Files.exists(srcPath)) {
                try (Stream<Path> paths = Files.walk(srcPath)) {
                    javaFiles = paths
                            .filter(Files::isRegularFile)
                            .filter(p -> p.toString().endsWith(".java"))
                            .map(srcPath::relativize)
                            .map(Path::toString)
                            .map(s -> s.replace("\\", "/"))
                            .collect(Collectors.toList());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return javaFiles;
    }
    
    /**
     * Obtient le contexte de l'application pour le chat AI
     */
    public String getApplicationContext() {
        StringBuilder context = new StringBuilder();
        
        context.append("STRUCTURE DU PROJET:\n");
        List<String> javaFiles = listJavaFiles();
        context.append("Fichiers Java trouvés: ").append(javaFiles.size()).append("\n\n");
        
        context.append("MODULES PRINCIPAUX:\n");
        context.append("- com.comptafx.dao: Accès aux données (DatabaseConfig, JournalEntryDAO, InvoiceDAO)\n");
        context.append("- com.comptafx.entities: Entités métier (JournalEntry, Invoice, etc.)\n");
        context.append("- com.comptafx.metier: Services métier (ServiceEcritures, ServiceFactures)\n");
        context.append("- com.comptafx.presentation: Contrôleurs JavaFX (MainController, ControleurEcritures, ControleurFactures)\n\n");
        
        context.append("FICHIERS CLÉS:\n");
        for (String file : javaFiles) {
            if (file.contains("Controller") || file.contains("DAO") || file.contains("Service")) {
                context.append("- ").append(file).append("\n");
            }
        }
        
        return context.toString();
    }
    
    /**
     * Obtient le contenu d'un fichier spécifique pour le contexte
     */
    public String getFileContext(String relativePath) {
        String content = readFile(relativePath);
        return String.format("CONTENU DU FICHIER %s:\n%s", relativePath, content);
    }
}

