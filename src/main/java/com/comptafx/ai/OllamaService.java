package com.comptafx.ai;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Service pour communiquer avec l'API Ollama
 */
public class OllamaService {
    
    private static final String OLLAMA_API_KEY = "497a071b3acc4644b36cfb5c22cc9789.E0xzKti_aNzY37AAosV2bejb";
    private static final String OLLAMA_CLOUD_URL = "https://api.ollama.com/v1";
    private static final String OLLAMA_LOCAL_URL = "http://localhost:11434/api";
    private String baseUrl;
    
    private final HttpClient httpClient;
    private final Gson gson;
    private final String model;
    private boolean useCloud;
    
    public OllamaService() {
        this("llama3.2");
    }
    
    public OllamaService(String model) {
        this.model = model;
        this.useCloud = false; // Commencer par local
        this.baseUrl = OLLAMA_LOCAL_URL;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.gson = new Gson();
        System.out.println("OllamaService initialisé avec URL: " + baseUrl + ", modèle: " + model);
    }
    
    public OllamaService(String model, boolean useCloud) {
        this.model = model;
        this.useCloud = useCloud;
        this.baseUrl = useCloud ? OLLAMA_CLOUD_URL : OLLAMA_LOCAL_URL;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.gson = new Gson();
        System.out.println("OllamaService initialisé avec URL: " + baseUrl + ", modèle: " + model);
    }
    
    /**
     * Essaie de basculer vers l'API cloud si local n'est pas disponible
     */
    private void trySwitchToCloud() {
        if (!useCloud) {
            System.out.println("Tentative de bascule vers l'API cloud...");
            this.useCloud = true;
            this.baseUrl = OLLAMA_CLOUD_URL;
        }
    }
    
    /**
     * Construit le corps de la requête JSON
     */
    private JsonObject buildRequestBody(String userMessage, List<ChatMessage> conversationHistory) {
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("model", model);
        
        JsonArray messages = new JsonArray();
        
        // Ajouter l'historique de conversation
        for (ChatMessage msg : conversationHistory) {
            JsonObject msgObj = new JsonObject();
            msgObj.addProperty("role", msg.getRole());
            msgObj.addProperty("content", msg.getContent());
            messages.add(msgObj);
        }
        
        // Ajouter le message utilisateur actuel
        JsonObject userMsg = new JsonObject();
        userMsg.addProperty("role", "user");
        userMsg.addProperty("content", userMessage);
        messages.add(userMsg);
        
        requestBody.add("messages", messages);
        requestBody.addProperty("stream", false);
        
        return requestBody;
    }
    
    /**
     * Envoie une requête HTTP à Ollama
     */
    private String sendHttpRequest(JsonObject requestBody, String url) throws Exception {
        String requestJson = gson.toJson(requestBody);
        System.out.println("Ollama Request URL: " + url + "/chat");
        System.out.println("Ollama Request Body: " + requestJson);
        
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(url + "/chat"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestJson))
                .timeout(Duration.ofSeconds(60));
        
        // Ajouter l'autorisation seulement pour l'API cloud
        if (url.equals(OLLAMA_CLOUD_URL)) {
            requestBuilder.header("Authorization", "Bearer " + OLLAMA_API_KEY);
        }
        
        HttpRequest request = requestBuilder.build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        System.out.println("Ollama Response Status: " + response.statusCode());
        System.out.println("Ollama Response Body: " + response.body());
        
        if (response.statusCode() == 200) {
            JsonObject jsonResponse = gson.fromJson(response.body(), JsonObject.class);
            
            // Format Ollama standard: {"message": {"role": "assistant", "content": "..."}}
            if (jsonResponse.has("message")) {
                JsonObject message = jsonResponse.getAsJsonObject("message");
                if (message.has("content")) {
                    return message.get("content").getAsString();
                }
            }
            
            // Format alternatif: {"response": "..."}
            if (jsonResponse.has("response")) {
                return jsonResponse.get("response").getAsString();
            }
            
            return "Format de réponse inattendu. Réponse complète: " + jsonResponse.toString();
        } else {
            String errorBody = response.body();
            throw new RuntimeException("Erreur HTTP " + response.statusCode() + ": " + 
                   (errorBody != null && !errorBody.isEmpty() ? errorBody : "Aucun détail disponible"));
        }
    }
    
    /**
     * Envoie un message au modèle Ollama et retourne la réponse
     */
    public CompletableFuture<String> sendMessage(String userMessage, List<ChatMessage> conversationHistory) {
        return CompletableFuture.supplyAsync(() -> {
            JsonObject requestBody = buildRequestBody(userMessage, conversationHistory);
            
            try {
                // Essayer avec l'URL actuelle
                return sendHttpRequest(requestBody, baseUrl);
            } catch (java.net.http.HttpTimeoutException e) {
                System.err.println("Ollama Timeout: " + e.getMessage());
                return "Timeout: La requête a pris trop de temps. Vérifiez votre connexion.";
            } catch (java.net.ConnectException e) {
                String errorMsg = e.getMessage() != null ? e.getMessage() : "Connection refused";
                System.err.println("Ollama Connection Error: " + errorMsg);
                // Si on était sur local et que ça échoue, essayer cloud une fois
                if (baseUrl.equals(OLLAMA_LOCAL_URL) && !useCloud) {
                    System.out.println("Basculement vers l'API cloud...");
                    trySwitchToCloud();
                    // Réessayer avec cloud
                    try {
                        return sendHttpRequest(requestBody, baseUrl);
                    } catch (Exception retryException) {
                        String retryMsg = retryException.getMessage() != null ? retryException.getMessage() : retryException.getClass().getSimpleName();
                        System.err.println("Tentative cloud échouée: " + retryMsg);
                    }
                }
                String suggestion = baseUrl.equals(OLLAMA_LOCAL_URL) 
                    ? "Vérifiez que Ollama est démarré localement (http://localhost:11434)\n" +
                      "Pour démarrer Ollama:\n" +
                      "1. Ouvrez une invite de commande\n" +
                      "2. Tapez: ollama serve\n" +
                      "3. Dans une autre fenêtre: ollama pull llama3.2"
                    : "Vérifiez votre connexion Internet et que l'API cloud est accessible";
                return "Erreur de connexion: Impossible de se connecter à l'API Ollama.\n" + 
                       "Détails: " + errorMsg + "\n\n" + suggestion;
            } catch (java.net.UnknownHostException e) {
                System.err.println("Ollama Unknown Host: " + e.getMessage());
                return "Erreur: Impossible de résoudre l'adresse de l'API Ollama. " +
                       "Vérifiez que l'URL est correcte: " + baseUrl;
            } catch (Exception e) {
                String errorMsg = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
                System.err.println("Ollama Error: " + e.getClass().getSimpleName() + " - " + errorMsg);
                if (e.getCause() != null) {
                    System.err.println("Cause: " + e.getCause().getMessage());
                }
                e.printStackTrace();
                return "Erreur inattendue lors de la communication avec Ollama: " + errorMsg + 
                       "\n\nVérifiez que:\n" +
                       "1. Ollama est installé et démarré (ollama serve)\n" +
                       "2. Le modèle llama3.2 est téléchargé (ollama pull llama3.2)\n" +
                       "3. Ollama écoute sur http://localhost:11434";
            }
        });
    }
    
    /**
     * Envoie un message avec contexte de l'application
     */
    public CompletableFuture<String> sendMessageWithContext(String userMessage, 
                                                             List<ChatMessage> conversationHistory,
                                                             String applicationContext) {
        String contextualMessage = buildContextualPrompt(userMessage, applicationContext);
        return sendMessage(contextualMessage, conversationHistory);
    }
    
    /**
     * Construit un prompt avec le contexte de l'application
     */
    private String buildContextualPrompt(String userMessage, String applicationContext) {
        return String.format("""
            Tu es un assistant AI intégré dans une application comptable JavaFX (ComptaFX).
            
            CONTEXTE DE L'APPLICATION:
            %s
            
            INSTRUCTIONS:
            - Tu peux aider à modifier le code de l'application
            - Tu peux suggérer des améliorations
            - Tu peux répondre aux questions sur l'application
            - Réponds en français de manière claire et concise
            
            QUESTION DE L'UTILISATEUR:
            %s
            """, applicationContext, userMessage);
    }
    
    /**
     * Teste la connexion à l'API Ollama, essaie local puis cloud si nécessaire
     */
    public CompletableFuture<Boolean> testConnection() {
        return CompletableFuture.supplyAsync(() -> {
            // Essayer local d'abord
            if (!useCloud) {
                try {
                    String testUrl = OLLAMA_LOCAL_URL.replace("/api", "") + "/api/tags";
                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create(testUrl))
                            .GET()
                            .timeout(Duration.ofSeconds(3))
                            .build();
                    
                    HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                    if (response.statusCode() == 200) {
                        System.out.println("✓ Connexion Ollama locale réussie");
                        return true;
                    }
                } catch (Exception e) {
                    System.out.println("✗ Ollama local non disponible: " + e.getMessage());
                }
                
                // Si local échoue, essayer cloud
                System.out.println("Tentative de connexion à l'API cloud...");
                trySwitchToCloud();
            }
            
            // Tester cloud
            try {
                String testUrl = OLLAMA_CLOUD_URL + "/models";
                HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                        .uri(URI.create(testUrl))
                        .header("Authorization", "Bearer " + OLLAMA_API_KEY)
                        .GET()
                        .timeout(Duration.ofSeconds(5));
                
                HttpRequest request = requestBuilder.build();
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    System.out.println("✓ Connexion Ollama cloud réussie");
                    return true;
                } else {
                    System.out.println("✗ Ollama cloud erreur: " + response.statusCode());
                }
            } catch (Exception e) {
                System.err.println("✗ Ollama cloud non disponible: " + e.getMessage());
            }
            
            return false;
        });
    }
    
    /**
     * Retourne l'URL de base utilisée
     */
    public String getBaseUrl() {
        return baseUrl;
    }
    
    /**
     * Classe pour représenter un message de chat
     */
    public static class ChatMessage {
        private final String role; // "user" or "assistant"
        private final String content;
        
        public ChatMessage(String role, String content) {
            this.role = role;
            this.content = content;
        }
        
        public String getRole() {
            return role;
        }
        
        public String getContent() {
            return content;
        }
    }
}