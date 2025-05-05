package org.novalearn.controllers.reclamation;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import org.novalearn.Entity.Reclamation;
import org.novalearn.service.ReclamationService;

import java.sql.SQLException;
import java.util.List;

public class ReclamationController {


    private final ReclamationService reclamationService;
    private ObservableList<Reclamation> reclamationObservableList;

    @FXML
    private ListView<Reclamation> reclamationListView;
    @FXML
    private AnchorPane mainContent;

    public ReclamationController() throws SQLException {
        this.reclamationService = new ReclamationService();
    }

    @FXML
    public void initialize() {


        // Rafraîchir la liste des Cours au démarrage
        refreshCoursList();
    }

    @FXML
    private FlowPane reclamationFlowPane;
    public void refreshCoursList() {
        try {
            reclamationFlowPane.getChildren().clear();

            List<Reclamation> reclamationList = reclamationService.readAll();

            for (Reclamation item : reclamationList) {
                VBox card = new VBox(10);
                card.setStyle("-fx-background-color: #ffffff; -fx-border-color: #ccc; -fx-border-radius: 10px;" +
                        " -fx-background-radius: 10px; -fx-padding: 10px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 4);");
                card.setPrefWidth(280);

                Label titre = new Label(item.getTitle());
                titre.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #222;");

                Label desc = new Label(item.getDescription());
                desc.setWrapText(true);
                desc.setMaxWidth(260);
                desc.setStyle("-fx-font-size: 13px; -fx-text-fill: #444;");

                Label priority = new Label("Priorité: " + item.getPriority());
                priority.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");

                Label currentStatus = new Label("Statut actuel: " + item.getStatus());
                currentStatus.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");

                // --- Nouveau : ComboBox pour changer le statut
                ComboBox<String> statusComboBox = new ComboBox<>();
                statusComboBox.getItems().addAll("En_attente", "En_cours", "Traiter");
                statusComboBox.setValue(item.getStatus()); // Par défaut : statut actuel

                // --- Nouveau : Bouton pour sauvegarder
                Button updateStatusButton = new Button("Mettre à jour");
                updateStatusButton.setOnAction(event -> {
                    String selectedStatus = statusComboBox.getValue();
                    try {
                        reclamationService.updateStatus(item.getId(), selectedStatus); // On appelle un service
                        showAlert("Succès", "Le statut a été mis à jour avec succès.", Alert.AlertType.INFORMATION);
                        refreshCoursList(); // Refresh la liste
                    } catch (SQLException e) {
                        showAlert("Erreur", "Échec de la mise à jour du statut : " + e.getMessage(), Alert.AlertType.ERROR);
                        e.printStackTrace();
                    }
                });

                // Ajouter tout dans la carte
                card.getChildren().addAll(titre, desc, priority, currentStatus, statusComboBox, updateStatusButton);

                reclamationFlowPane.getChildren().add(card);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private VBox buildCoursCard(Reclamation item) {
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: #ffffff; -fx-border-color: #ccc; -fx-border-radius: 10px;" +
                " -fx-background-radius: 10px; -fx-padding: 10px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 4);");
        card.setPrefWidth(280);


        Label titre = new Label(item.getTitle());
        titre.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #222;");

        Label desc = new Label(item.getDescription());
        desc.setWrapText(true);
        desc.setMaxWidth(260);
        desc.setStyle("-fx-font-size: 13px; -fx-text-fill: #444;");

        Label periority = new Label("Priority: " + item.getPriority());
        periority.setWrapText(true);
        periority.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");

        Label genre = new Label("Auteur ID: " + item.getGenreId());
        genre.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");

        Label status = new Label("Téléchargements: " + item.getStatus());
        status.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");

        Label created = new Label("Likes: " + item.getCreatedAt());
        created.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");





        card.getChildren().addAll(titre, desc, periority, status,genre,created);

        return card;
    }

    private void updateCoursFlowPane(List<Reclamation> ReclamationList) {
        reclamationFlowPane.getChildren().clear();
        for (Reclamation item : ReclamationList) {
            VBox card = buildCoursCard(item);
            reclamationFlowPane.getChildren().add(card);
        }
    }


}
