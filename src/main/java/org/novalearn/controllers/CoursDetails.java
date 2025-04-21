package org.novalearn.controllers;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import org.novalearn.Entity.Cours;
import org.novalearn.Entity.Exercice;
import org.novalearn.services.coursEexercice.CoursService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class CoursDetails {
    private final CoursService coursService;
    @FXML
    private VBox exercicesBox;
    @FXML
    private VBox rootVBox;
    @FXML
    private StackPane videoPane;
    @FXML private MediaView mediaView;
    @FXML private Label titre, desc, contenu, duration;
    @FXML private HBox stars;
    @FXML private Button exercice;

    private MediaPlayer mediaPlayer;

    public CoursDetails() throws SQLException {
        this.coursService = new CoursService();
    }

    public void setCoursData(Cours item) {
        titre.setText("Voir la course: " + item.getTitre());
        desc.setText(item.getDescription());
        contenu.setText(item.getContenu());

        duration.setText("Durée: 3h 20m");

        // Star rating
        stars.getChildren().clear();
        int rating = Math.min(5, Math.max(1, item.getNbrLike() / 10));
        for (int i = 0; i < 5; i++) {
            Label star = new Label(i < rating ? "★" : "☆");
            stars.setStyle("-fx-text-fill: #FFD700; -fx-font-size: 14px;");
            stars.getChildren().add(star);
        }

        MediaView mediaView = null;

        if (item.getVideo() != null) {
            try {
                File tempVideo = File.createTempFile("video", ".mp4");
                tempVideo.deleteOnExit();

                try (FileOutputStream fos = new FileOutputStream(tempVideo)) {
                    fos.write(item.getVideo());
                }
                Media media = new Media(tempVideo.toURI().toString());
                MediaPlayer mediaPlayer = new MediaPlayer(media);
                mediaView = new MediaView(mediaPlayer);

                mediaView.setPreserveRatio(true);



                Button playButton = new Button("▶");
                playButton.setStyle("-fx-font-size:35px; -fx-background-color: rgba(255,255,255,0.7);");
                playButton.setOnAction(e -> {
                    mediaPlayer.play();
                    playButton.setVisible(false);
                });



                videoPane.getChildren().addAll(mediaView, playButton);
                videoPane.setAlignment(playButton, Pos.CENTER);



            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Erreur", "Impossible de lire la vidéo.", Alert.AlertType.ERROR);
            }
        }
        exercice.setOnAction(event -> {
            try {
                List<Exercice> exercices = coursService.getExercicesByCoursId(item.getId());
                exercicesBox.getChildren().clear(); // vider d'abord
                exercicesBox.setVisible(true);

                if (exercices.isEmpty()) {
                    Label noExLabel = new Label("Aucun exercice trouvé pour ce cours.");
                    noExLabel.setStyle("-fx-text-fill: #f30a0a;");
                    exercicesBox.getChildren().add(noExLabel);
                } else {
                    for (Exercice ex : exercices) {
                        VBox card = new VBox(10);
                        card.setStyle("-fx-background-color: rgba(224,149,149,0.74); -fx-padding: 10; -fx-background-radius: 8;");
                        Label titre = new Label(ex.getTitre());
                        titre.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");
                        Label desc = new Label(ex.getDescription());
                        desc.setWrapText(true);
                        Label date = new Label("Créé le: " + ex.getCreatedAt());
                        date.setStyle("-fx-font-size: 11px; -fx-text-fill: #0a692b;");

                        card.getChildren().addAll(titre, desc, date);
                        exercicesBox.getChildren().add(card);
                    }
                }

            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Erreur", "Impossible de charger les exercices.", Alert.AlertType.ERROR);
            }
        });
    }
    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
