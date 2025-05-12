package org.novalearn.controllers.cours;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class cours_app {
    @FXML private TextArea promptArea;
    @FXML private TextArea responseArea;

    // reuse a single ObjectMapper for JSON serialization
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient client = HttpClient.newHttpClient();

    @FXML
    private void onSubmit() {
        String prompt = promptArea.getText().trim();
        if (prompt.isEmpty()) {
            responseArea.setText("Veuillez entrer un texte.");
            return;
        }
        submitRequest(prompt);
    }

    private void submitRequest(String prompt) {
        try {
            // 1) Build your JSON payload with both fields
            Map<String, String> bodyMap = Map.of(
                    "prompt",      prompt,
                    "description", prompt   // or any other description you want
            );
            String jsonPayload = objectMapper.writeValueAsString(bodyMap);

            // 2) Create the HTTP request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8003/generate"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload, StandardCharsets.UTF_8))
                    .build();

            // 3) Send asynchronously
            CompletableFuture.supplyAsync(() -> {
                try {
                    System.out.println("▶ Sending payload: " + jsonPayload);
                    HttpResponse<String> resp = client.send(request, HttpResponse.BodyHandlers.ofString());
                    System.out.println("◀ Received response: " + resp.body());
                    return resp.body();
                } catch (Exception e) {
                    e.printStackTrace();
                    return "{\"error\":\"Erreur lors de l'appel à l'API.\"}";
                }
            }).thenAccept(responseJson -> {
                String extracted = extractTextFromJson(responseJson);
                // replace literal "\n" with real newlines
                responseArea.setText(extracted.replace("\\n", "\n"));
            });

        } catch (Exception e) {
            e.printStackTrace();
            responseArea.setText("Erreur lors de la création de la requête JSON.");
        }
    }

    private String extractTextFromJson(String jsonResponse) {
        // very basic parsing; consider using Jackson here too
        int idx = jsonResponse.indexOf("\"text\":");
        if (idx < 0) {
            return "Texte non disponible ou format inattendu : " + jsonResponse;
        }
        int start = jsonResponse.indexOf('"', idx + 7) + 1;
        int end   = jsonResponse.indexOf('"', start);
        if (start < 0 || end < 0) {
            return "Erreur de parsing de la réponse : " + jsonResponse;
        }
        return jsonResponse.substring(start, end);
    }
}
