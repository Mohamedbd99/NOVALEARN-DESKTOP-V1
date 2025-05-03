package org.novalearn.controllers;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

public class QuizApp extends Application {

    @Override
    public void start(Stage stage) {
        // Créer les éléments de l'interface
        Label promptLabel = new Label("Entrez votre texte:");
        TextArea promptArea = new TextArea();
        promptArea.setPromptText("Entrez votre texte ici...");
        Button submitButton = new Button("Soumettre");
        TextArea responseArea = new TextArea();
        responseArea.setEditable(false);
        responseArea.setPromptText("La réponse de l'API sera affichée ici...");

        // Gestion du clic sur le bouton pour soumettre le texte
        submitButton.setOnAction(e -> {
            String prompt = promptArea.getText();
            if (prompt.trim().isEmpty()) {
                responseArea.setText("Veuillez entrer un texte.");
            } else {
                submitRequest(prompt, responseArea);
            }
        });

        // Organiser les éléments dans un VBox
        VBox vbox = new VBox(10, promptLabel, promptArea, submitButton, responseArea);
        Scene scene = new Scene(vbox, 400, 400);

        stage.setTitle("Consommateur API");
        stage.setScene(scene);
        stage.show();
    }

    private void submitRequest(String prompt, TextArea responseArea) {
        // URL de l'API FastAPI
        String url = "http://localhost:8001/generate";

        // Créer la requête HTTP
        String jsonPayload = "{ \"prompt\": \"" + prompt + "\" }";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload, StandardCharsets.UTF_8))
                .build();

        HttpClient client = HttpClient.newHttpClient();

        // Envoyer la requête de manière asynchrone
        CompletableFuture.supplyAsync(() -> {
            try {
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                return response.body();
            } catch (Exception e) {
                e.printStackTrace();
                return "Erreur lors de l'appel à l'API.";
            }
        }).thenAccept(response -> {
            // Extraire le texte de la réponse JSON
            String extractedText = extractTextFromJson(response);

            // Mettre à jour l'interface avec la réponse extraites
            responseArea.setText(extractedText.replace("\\n", "\n").replace("\n", System.lineSeparator()));
        });
    }

    // Méthode pour extraire le texte utile à partir de la réponse JSON
    private String extractTextFromJson(String jsonResponse) {
        // Supposons que la réponse JSON ressemble à ce format :
        // {"text": "Votre texte ici", ...}
        int startIndex = jsonResponse.indexOf("\"text\": \"") + 9; // 9 est la longueur de la chaîne "\"text\": \""
        int endIndex = jsonResponse.indexOf("\"}", startIndex);

        if (startIndex != -1 && endIndex != -1) {
            return jsonResponse.substring(startIndex, endIndex);
        }

        return "Texte non disponible ou erreur dans le format de la réponse.";
    }


    public static void main(String[] args) {
        launch();
    }
}
