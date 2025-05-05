package org.novalearn.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

public class cours_app {
    @FXML
    private TextArea promptArea;
    @FXML private TextArea responseArea;

    @FXML
    private void onSubmit() {
        String prompt = promptArea.getText();
        if (prompt.trim().isEmpty()) {
            responseArea.setText("Veuillez entrer un texte.");
        } else {
            submitRequest(prompt);
        }
    }

    private void submitRequest(String prompt) {
        String url = "http://localhost:8001/generate";
        String jsonPayload = "{ \"prompt\": \"" + prompt + "\" }";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload, StandardCharsets.UTF_8))
                .build();

        HttpClient client = HttpClient.newHttpClient();

        CompletableFuture.supplyAsync(() -> {
            try {
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                return response.body();
            } catch (Exception e) {
                e.printStackTrace();
                return "Erreur lors de l'appel à l'API.";
            }
        }).thenAccept(response -> {
            String extractedText = extractTextFromJson(response);
            responseArea.setText(extractedText.replace("\\n", "\n").replace("\n", System.lineSeparator()));
        });
    }

    private String extractTextFromJson(String jsonResponse) {
        int startIndex = jsonResponse.indexOf("\"text\": \"") + 9;
        int endIndex = jsonResponse.indexOf("\"}", startIndex);

        if (startIndex != -1 && endIndex != -1) {
            return jsonResponse.substring(startIndex, endIndex);
        }

        return "Texte non disponible ou erreur dans le format de la réponse.";
    }

}
