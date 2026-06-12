package com.comptafx.presentation;

import com.comptafx.ai.CodeAccessService;
import com.comptafx.ai.OllamaService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Contrôleur pour le panneau de chat AI
 */
public class ChatController implements Initializable {
    
    @FXML private VBox messagesContainer;
    @FXML private TextArea messageInput;
    @FXML private Button sendButton;
    @FXML private ScrollPane messagesScrollPane;
    @FXML private Label statusLabel;
    @FXML private Button clearChatButton;
    @FXML private Button toggleChatButton;
    
    private final OllamaService ollamaService;
    private final CodeAccessService codeAccessService;
    private final List<OllamaService.ChatMessage> conversationHistory;
    private boolean isCollapsed = false;
    
    public ChatController() {
        this.ollamaService = new OllamaService();
        this.codeAccessService = new CodeAccessService();
        this.conversationHistory = new ArrayList<>();
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialiser avec un message de bienvenue
        addWelcomeMessage();
        
        // Configurer le scroll automatique avec un listener
        messagesContainer.heightProperty().addListener((obs, oldHeight, newHeight) -> {
            Platform.runLater(() -> {
                messagesScrollPane.setVvalue(1.0);
            });
        });
        
        // Tester la connexion à Ollama au démarrage
        testOllamaConnection();
    }
    
    private void testOllamaConnection() {
        statusLabel.setText("Test de connexion...");
        ollamaService.testConnection().thenAccept(connected -> {
            Platform.runLater(() -> {
                if (connected) {
                    String url = ollamaService.getBaseUrl();
                    String source = url.contains("localhost") ? "Local" : "Cloud";
                    statusLabel.setText("✓ Connecté à Ollama (" + source + ")");
                    addSystemMessage("✓ Connexion établie avec Ollama " + source + "\n" +
                            "Vous pouvez maintenant poser vos questions !");
                } else {
                    statusLabel.setText("⚠ Ollama non accessible");
                    addSystemMessage("⚠ Impossible de se connecter à Ollama.\n\n" +
                            "OPTIONS:\n\n" +
                            "Option 1 - Ollama Local (Recommandé):\n" +
                            "1. Téléchargez Ollama: https://ollama.com\n" +
                            "2. Installez et démarrez Ollama\n" +
                            "3. Téléchargez un modèle: ollama pull llama3.2\n" +
                            "4. Redémarrez cette application\n\n" +
                            "Option 2 - Utiliser l'API Cloud:\n" +
                            "Vérifiez que votre clé API est valide et que vous avez une connexion Internet.\n\n" +
                            "Le chat essaiera automatiquement de se reconnecter lors de votre prochaine question.");
                }
            });
        });
    }
    
    @FXML
    private void handleSendMessage() {
        String message = messageInput.getText().trim();
        if (message.isEmpty()) {
            return;
        }
        
        // Ajouter le message utilisateur à l'interface
        addUserMessage(message);
        messageInput.clear();
        
        // Afficher le statut de chargement
        statusLabel.setText("Envoi en cours...");
        sendButton.setDisable(true);
        
        // Obtenir le contexte de l'application
        String appContext = codeAccessService.getApplicationContext();
        
        // Envoyer le message à Ollama avec contexte
        ollamaService.sendMessageWithContext(message, new ArrayList<>(conversationHistory), appContext)
                .thenAccept(response -> Platform.runLater(() -> {
                    if (response != null && !response.isEmpty()) {
                        addAssistantMessage(response);
                        statusLabel.setText("Prêt");
                    } else {
                        addAssistantMessage("Désolé, je n'ai pas pu obtenir de réponse. Vérifiez votre connexion à l'API Ollama.");
                        statusLabel.setText("Erreur de connexion");
                    }
                    sendButton.setDisable(false);
                }))
                .exceptionally(throwable -> {
                    Platform.runLater(() -> {
                        String errorMsg = throwable.getCause() != null ? 
                            throwable.getCause().getMessage() : throwable.getMessage();
                        addAssistantMessage("❌ Erreur: " + errorMsg + 
                            "\n\nVérifiez que:\n" +
                            "• L'API Ollama est accessible\n" +
                            "• Votre clé API est valide\n" +
                            "• Votre connexion Internet fonctionne");
                        statusLabel.setText("Erreur");
                        sendButton.setDisable(false);
                    });
                    return null;
                });
        
        // Ajouter à l'historique
        conversationHistory.add(new OllamaService.ChatMessage("user", message));
    }
    
    @FXML
    private void handleKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            if (event.isShiftDown()) {
                // Shift+Enter pour nouvelle ligne
                return;
            } else {
                // Enter seul pour envoyer
                event.consume();
                handleSendMessage();
            }
        }
    }
    
    @FXML
    private void handleClearChat() {
        conversationHistory.clear();
        messagesContainer.getChildren().clear();
        addWelcomeMessage();
    }
    
    @FXML
    private void handleToggleChat() {
        // Cette fonctionnalité sera gérée par le MainController
        // pour masquer/afficher le panneau
    }
    
    @FXML
    private void handleShowCode() {
        String codeContext = codeAccessService.getApplicationContext();
        addSystemMessage("Structure du code:\n" + codeContext);
    }
    
    @FXML
    private void handleShowStructure() {
        List<String> files = codeAccessService.listJavaFiles();
        StringBuilder structure = new StringBuilder("Fichiers Java du projet:\n\n");
        for (String file : files) {
            structure.append("• ").append(file).append("\n");
        }
        addSystemMessage(structure.toString());
    }
    
    @FXML
    private void handleShowHelp() {
        String help = """
            Commandes disponibles:
            
            • Tapez votre question normalement pour obtenir de l'aide
            • Utilisez "📋 Code" pour voir la structure du code
            • Utilisez "📁 Structure" pour lister tous les fichiers
            • Le chat a accès au code source de l'application
            
            Exemples de questions:
            - "Comment fonctionne la création d'écritures?"
            - "Montre-moi le code de DatabaseConfig"
            - "Comment ajouter une nouvelle fonctionnalité?"
            """;
        addSystemMessage(help);
    }
    
    private void addWelcomeMessage() {
        String welcome = """
            👋 Bonjour ! Je suis votre assistant AI intégré.
            
            Je peux vous aider à:
            • Comprendre le code de l'application
            • Modifier et améliorer le code
            • Répondre à vos questions
            • Suggérer des améliorations
            
            Posez-moi une question ou utilisez les boutons ci-dessous pour commencer !
            """;
        addAssistantMessage(welcome);
    }
    
    private void addUserMessage(String message) {
        HBox messageBox = createMessageBox(message, "user");
        messagesContainer.getChildren().add(messageBox);
        scrollToBottom();
    }
    
    private void addAssistantMessage(String message) {
        HBox messageBox = createMessageBox(message, "assistant");
        messagesContainer.getChildren().add(messageBox);
        
        // Ajouter à l'historique
        conversationHistory.add(new OllamaService.ChatMessage("assistant", message));
        
        scrollToBottom();
    }
    
    private void addSystemMessage(String message) {
        HBox messageBox = createMessageBox(message, "system");
        messagesContainer.getChildren().add(messageBox);
        scrollToBottom();
    }
    
    private HBox createMessageBox(String message, String role) {
        HBox container = new HBox(10);
        container.setAlignment(role.equals("user") ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        container.setMaxWidth(Double.MAX_VALUE);
        
        VBox messageBox = new VBox(5);
        messageBox.getStyleClass().add("chat-message");
        messageBox.getStyleClass().add("chat-message-" + role);
        messageBox.setMaxWidth(300); // Limiter la largeur du messageBox
        messageBox.setPrefWidth(280);
        
        // Avatar/Icon
        Label avatar = new Label(role.equals("user") ? "👤" : role.equals("system") ? "ℹ️" : "🤖");
        avatar.getStyleClass().add("chat-avatar");
        
        // Message content - utiliser Label avec wrapText
        Label messageLabel = new Label(message);
        messageLabel.setWrapText(true);
        messageLabel.getStyleClass().add("chat-text");
        
        // Fix: Définir une largeur fixe explicite (sans binding pour éviter les problèmes)
        // La largeur sera ajustée après le layout si nécessaire
        messageLabel.setMaxWidth(280);
        messageLabel.setMinWidth(200);
        messageLabel.setPrefWidth(280);
        
        // Ajuster après le layout pour s'adapter au conteneur
        Platform.runLater(() -> {
            double containerWidth = messagesContainer.getWidth();
            if (containerWidth > 0) {
                double targetWidth = Math.min(280, Math.max(200, containerWidth * 0.7 - 50));
                messageLabel.setMaxWidth(targetWidth);
                messageLabel.setPrefWidth(targetWidth);
            }
        });
        
        messageBox.getChildren().add(messageLabel);
        messageBox.setPadding(new Insets(10, 15, 10, 15));
        
        if (role.equals("user")) {
            container.getChildren().addAll(messageBox, avatar);
        } else {
            container.getChildren().addAll(avatar, messageBox);
        }
        
        return container;
    }
    
    private void scrollToBottom() {
        Platform.runLater(() -> {
            // Unbind if bound, then set the value
            if (messagesScrollPane.vvalueProperty().isBound()) {
                messagesScrollPane.vvalueProperty().unbind();
            }
            messagesScrollPane.setVvalue(1.0);
        });
    }
    
    public void setToggleAction(Runnable action) {
        toggleChatButton.setOnAction(e -> action.run());
    }
    
    public boolean isCollapsed() {
        return isCollapsed;
    }
    
    public void setCollapsed(boolean collapsed) {
        isCollapsed = collapsed;
        toggleChatButton.setText(collapsed ? "▶" : "◀");
    }
}

