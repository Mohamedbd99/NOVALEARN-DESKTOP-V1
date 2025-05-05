package org.novalearn.controllers.cours;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.novalearn.Entity.Cours;
import org.novalearn.Entity.Exercice;
import org.novalearn.service.coursEexercice.CoursService;

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
    private Cours currentCours;

    public CoursDetails() throws SQLException {
        this.coursService = new CoursService();
    }

    public void setCoursData(Cours item) {
        this.currentCours = item;

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
    @FXML
    private void handleDownloadPdf() {
        try {
            // Ouvrir un FileChooser pour sélectionner le répertoire où l'on souhaite enregistrer le PDF
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers PDF", "*.pdf"));
            File file = fileChooser.showSaveDialog(new Stage());

            if (file != null) {
                // Appeler la méthode pour générer le PDF avec le titre et la description du cours
                generatePdf(file);
                CoursService coursService = new CoursService();
                coursService.incrementDownloadCount(currentCours.getId());
            }
        } catch (IOException | DocumentException | SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de télécharger le fichier PDF.", Alert.AlertType.ERROR);
        }
    }

    // Générer le fichier PDF
    private void generatePdf(File file) throws IOException, DocumentException {
        Document document = new Document();
        FileOutputStream fos = new FileOutputStream(file);
        PdfWriter.getInstance(document, fos);
        document.open();

        // Ajouter le logo (assurez-vous que le chemin est correct)
        String logoPath = "src/main/resources/org/novalearn/logooo.png";
        try {
            Image logo = Image.getInstance(logoPath);
            logo.scaleToFit(100, 100);
            logo.setAlignment(Element.ALIGN_CENTER);
            document.add(logo);
        } catch (Exception e) {
            System.out.println("Erreur lors du chargement du logo: " + e.getMessage());
        }

        document.add(Chunk.NEWLINE);

        // Style pour le titre
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, BaseColor.DARK_GRAY);
        Paragraph title = new Paragraph("📘 " + titre.getText(), titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        document.add(Chunk.NEWLINE);

        // Style pour la description
        Font descFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL, BaseColor.BLACK);
        Paragraph contenuu = new Paragraph(contenu.getText(), descFont);
        contenuu.setAlignment(Element.ALIGN_JUSTIFIED);
        document.add(contenuu);

        document.add(Chunk.NEWLINE);

        // Ajouter une image du cours si disponible
        if (currentCours.getImage() != null) {
            File imageTemp = File.createTempFile("cours_image", ".jpg");
            try (FileOutputStream imgOut = new FileOutputStream(imageTemp)) {
                imgOut.write(currentCours.getImage());
            }

            Image coursImage = Image.getInstance(imageTemp.getAbsolutePath());
            coursImage.scaleToFit(400, 300);
            coursImage.setAlignment(Element.ALIGN_CENTER);
            document.add(coursImage);
        }

        document.close();
        fos.close();

        showAlert("Succès", "Le PDF a été téléchargé avec succès.", Alert.AlertType.INFORMATION);
    }

    // Méthode pour afficher une alerte (déjà présente dans votre code)

}
